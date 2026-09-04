import re
from datetime import datetime, timedelta

file_path = r'd:\WorkSpace\Document\Improve-Knowledge\Plan\01-detailed-sessions.md'

with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

start_date = datetime(2026, 9, 2)

def get_day_name(dt):
    days = ['Thứ 2', 'Thứ 3', 'Thứ 4', 'Thứ 5', 'Thứ 6', 'Thứ 7', 'Chủ Nhật']
    return days[dt.weekday()]

def replace_week(match):
    old_start_str = match.group(2) + '/2026'
    old_start_dt = datetime.strptime(old_start_str, '%d/%m/%Y')
    days_diff = (old_start_dt - datetime(2026, 8, 10)).days
    
    new_start_dt = start_date + timedelta(days=days_diff)
    new_end_dt = new_start_dt + timedelta(days=6)
    
    return match.group(1) + new_start_dt.strftime('%d/%m') + match.group(3) + new_end_dt.strftime('%d/%m') + match.group(5)

content = re.sub(r'(<summary><b>Week \d+ \()(\d{2}/\d{2})( - )(\d{2}/\d{2})(\):.*?</b></summary>)', replace_week, content)

def replace_day(match):
    old_date_str = match.group(4) + '/2026'
    old_date_dt = datetime.strptime(old_date_str, '%d/%m/%Y')
    days_diff = (old_date_dt - datetime(2026, 9, 1)).days
    new_date_dt = start_date + timedelta(days=days_diff)
    
    new_day_name = get_day_name(new_date_dt)
    return match.group(1) + new_day_name + match.group(3) + new_date_dt.strftime('%d/%m') + match.group(5)

content = re.sub(r'(#### )(.*?)( \()(\d{2}/\d{2})(\))', replace_day, content)

end_date = start_date + timedelta(days=55)
content = content.replace('09/01/2026', start_date.strftime('%d/%m/%Y'))
content = content.replace('04/10/2026', end_date.strftime('%d/%m/%Y'))

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)
print('Cập nhật ngày thành công!')
