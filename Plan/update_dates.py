import re
from datetime import datetime, timedelta

# ============================================================
# update_dates.py — Cập nhật ngày trong INTERVIEW_COMEBACK_PLAN.md
#
# Dùng khi cần dời lịch bắt đầu sang ngày khác.
# Script sẽ tính lại TẤT CẢ ngày trong file dựa trên
# START_DATE mới, giữ nguyên khoảng cách ngày tương đối.
#
# Ngày gốc (origin) để tính khoảng cách: 28/09/2026
# ============================================================

FILE_PATH = r'd:\WorkSpace\Document\Improve-Knowledge\Plan\INTERVIEW_COMEBACK_PLAN.md'

# ⚙️ THAY ĐỔI NGÀY BẮT ĐẦU Ở ĐÂY (nếu cần dời lịch)
START_DATE = datetime(2026, 9, 28)   # Ngày bắt đầu mới

# Ngày gốc ban đầu của plan (KHÔNG thay đổi)
ORIGIN_DATE = datetime(2026, 9, 28)

# Ngày kết thúc 4 tuần (28 ngày sau khi bắt đầu)
END_DATE = START_DATE + timedelta(days=27)


def get_day_name(dt: datetime) -> str:
    """Trả về tên ngày tiếng Việt."""
    days = ['Thứ 2', 'Thứ 3', 'Thứ 4', 'Thứ 5', 'Thứ 6', 'Thứ 7', 'Chủ Nhật']
    return days[dt.weekday()]


def shift_date(old_date_str: str) -> datetime:
    """Tính ngày mới từ ngày cũ (DD/MM) dựa trên offset so với ORIGIN_DATE."""
    old_dt = datetime.strptime(f"{old_date_str}/2026", '%d/%m/%Y')
    days_diff = (old_dt - ORIGIN_DATE).days
    return START_DATE + timedelta(days=days_diff)


with open(FILE_PATH, 'r', encoding='utf-8') as f:
    content = f.read()

# -------------------------------------------------------
# 1. Cập nhật header `#### Thứ X (DD/MM)` và `#### Chủ Nhật (DD/MM)`
#    — đây là format mà generate_ics.py dùng để parse
# -------------------------------------------------------
def replace_day_header(match):
    old_date_str = match.group(2)          # e.g. "28/09"
    new_dt = shift_date(old_date_str)
    new_day_name = get_day_name(new_dt)
    return f"#### {new_day_name} ({new_dt.strftime('%d/%m')})"

content = re.sub(
    r'#### (Thứ \d+|Chủ Nhật) \((\d{2}/\d{2})\)',
    replace_day_header,
    content
)

# -------------------------------------------------------
# 2. Cập nhật Week header trong <summary> tag
#    Format: Week N (DD/MM - DD/MM): ...
# -------------------------------------------------------
def replace_week_header(match):
    old_start_str = match.group(2)         # e.g. "28/09"
    old_end_str   = match.group(4)         # e.g. "04/10"
    new_start_dt  = shift_date(old_start_str)
    new_end_dt    = shift_date(old_end_str)
    return (
        match.group(1)
        + new_start_dt.strftime('%d/%m')
        + match.group(3)
        + new_end_dt.strftime('%d/%m')
        + match.group(5)
    )

content = re.sub(
    r'(<summary><b>Week \d+ \()(\d{2}/\d{2})( - )(\d{2}/\d{2})(\):)',
    replace_week_header,
    content
)

# -------------------------------------------------------
# 3. Cập nhật TUẦN header trong kế hoạch tổng quan
#    Format: ### 🔴 TUẦN 1 — ... (DD/MM - DD/MM)
# -------------------------------------------------------
def replace_phase_header(match):
    old_start_str = match.group(2)
    old_end_str   = match.group(4)
    new_start_dt  = shift_date(old_start_str)
    new_end_dt    = shift_date(old_end_str)
    return (
        match.group(1)
        + new_start_dt.strftime('%d/%m')
        + match.group(3)
        + new_end_dt.strftime('%d/%m')
        + match.group(5)
    )

content = re.sub(
    r'(### [^\n]+ \()(\d{2}/\d{2})( - )(\d{2}/\d{2})(\))',
    replace_phase_header,
    content
)

# -------------------------------------------------------
# 4. Cập nhật các ngày đứng độc lập trong Daily Drill table
#    Format: | CN 28/09 | hoặc | T2 29/09 |
# -------------------------------------------------------
def replace_table_date(match):
    prefix        = match.group(1)         # e.g. "CN " or "T2 "
    old_date_str  = match.group(2)         # e.g. "28/09"
    new_dt        = shift_date(old_date_str)
    new_day_abbr  = {
        'Thứ 2': 'T2', 'Thứ 3': 'T3', 'Thứ 4': 'T4',
        'Thứ 5': 'T5', 'Thứ 6': 'T6', 'Thứ 7': 'T7', 'Chủ Nhật': 'CN'
    }.get(get_day_name(new_dt), prefix.strip())
    return f"| {new_day_abbr} {new_dt.strftime('%d/%m')} |"

content = re.sub(
    r'\| (CN|T[2-7]) (\d{2}/\d{2}) \|',
    replace_table_date,
    content
)

# -------------------------------------------------------
# 5. Cập nhật các mốc ngày nhắc đến trong text
#    Ngày bắt đầu và ngày kết thúc trong header
# -------------------------------------------------------
content = content.replace(
    ORIGIN_DATE.strftime('%d/%m/%Y'),
    START_DATE.strftime('%d/%m/%Y')
)
content = content.replace(
    (ORIGIN_DATE + timedelta(days=27)).strftime('%d/%m/%Y'),
    END_DATE.strftime('%d/%m/%Y')
)

# Cập nhật dòng "Ngày bắt đầu lại" và "target phỏng vấn"
content = re.sub(
    r'\*\*Ngày bắt đầu lại:\*\* \d{2}/\d{2}/\d{4}',
    f'**Ngày bắt đầu lại:** {START_DATE.strftime("%d/%m/%Y")}',
    content
)
content = re.sub(
    r'\*\*Ngày target phỏng vấn:\*\* \d{2}/\d{2}/\d{4}',
    f'**Ngày target phỏng vấn:** {END_DATE.strftime("%d/%m/%Y")} (4 tuần)',
    content
)

# Cập nhật dòng footer cuối file
content = re.sub(
    r'\*Cập nhật: \d{2}/\d{2}/\d{4}.*\*',
    f'*Cập nhật: {datetime.now().strftime("%d/%m/%Y")} — Comeback Plan sau thời gian break (4 tuần, target: {END_DATE.strftime("%d/%m/%Y")})*',
    content
)

with open(FILE_PATH, 'w', encoding='utf-8') as f:
    f.write(content)

print(f'✅ Cập nhật ngày thành công!')
print(f'   Bắt đầu: {START_DATE.strftime("%d/%m/%Y")} → Kết thúc: {END_DATE.strftime("%d/%m/%Y")}')
print(f'   File: {FILE_PATH}')
