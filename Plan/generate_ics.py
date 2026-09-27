import re
from datetime import datetime
import os

# ============================================================
# generate_ics.py — Tạo file lịch .ics từ INTERVIEW_COMEBACK_PLAN.md
#
# Đọc file markdown, parse các ngày theo format:
#   #### Thứ X (DD/MM)  hoặc  #### Chủ Nhật (DD/MM)
# Tạo các calendar event theo time block chuẩn:
#   - Weekday (T2-T6): 04:30, 05:30, 06:30, 20:00, 21:30
#   - Thứ 7 lẻ (đi làm): 04:30, 05:30, 06:30, 20:00
#   - Thứ 7 chẵn (nghỉ): 04:30, 05:30, 08:00, 10:00, 11:30, 13:30, 15:30, 17:00
#   - Chủ Nhật: 04:30, 05:30, 09:00, 14:00, 21:30 (nếu có)
# ============================================================

MD_PATH  = r'd:\WorkSpace\Document\Improve-Knowledge\Plan\INTERVIEW_COMEBACK_PLAN.md'
ICS_PATH = r'd:\WorkSpace\Document\Improve-Knowledge\Plan\study_schedule.ics'

YEAR = 2026  # Năm áp dụng cho tất cả ngày DD/MM


def clean_description(desc: str) -> str:
    """Làm sạch markdown và escape ký tự đặc biệt cho ICS."""
    if not desc:
        return ""
    desc = re.sub(r'\[([^\]]+)\]\(([^)]+)\)', r'\1 (\2)', desc)   # [text](url) → text (url)
    desc = desc.replace("**", "").replace("*", "").replace("`", "")
    desc = desc.replace("\\", "\\\\").replace(";", "\\;").replace(",", "\\,")
    desc = desc.replace("\n", "\\n")
    return desc.strip()


def find_bullet(bullets: list, *keywords) -> str | None:
    """Tìm bullet đầu tiên chứa bất kỳ keyword nào (case-insensitive)."""
    for b in bullets:
        cleaned = re.sub(r'^[*+\-]\s+', '', b).strip()
        if any(kw.lower() in cleaned.lower() for kw in keywords):
            return cleaned
    return None


def generate_ics():
    if not os.path.exists(MD_PATH):
        print(f"❌ Không tìm thấy file: {MD_PATH}")
        return

    with open(MD_PATH, "r", encoding="utf-8") as f:
        content = f.read()

    # Parse tất cả header ngày: #### Thứ X (DD/MM) hoặc #### Chủ Nhật (DD/MM)
    day_header_pat = re.compile(
        r'^####\s+(Thứ\s+\d+|Chủ\s+Nhật)\s+\((\d{2})/(\d{2})\)',
        re.MULTILINE
    )

    matches = list(day_header_pat.finditer(content))
    if not matches:
        print("❌ Không tìm thấy ngày nào. Kiểm tra format '#### Thứ X (DD/MM)' trong file.")
        return

    days = []
    for i, match in enumerate(matches):
        day_name  = match.group(1).strip()
        day_str   = match.group(2)
        month_str = match.group(3)
        date_iso  = f"{YEAR}-{month_str}-{day_str}"

        start_idx = match.end()
        end_idx   = matches[i + 1].start() if i + 1 < len(matches) else len(content)
        day_content = content[start_idx:end_idx]

        bullets = []
        for line in day_content.splitlines():
            line_str = line.strip()
            if line_str.startswith(("*", "-", "+")):
                bullets.append(line_str)

        days.append({
            "date":    date_iso,
            "name":    day_name,
            "bullets": bullets,
        })

    ics_events = []

    for day in days:
        date_iso = day["date"]
        bullets  = day["bullets"]
        dt       = datetime.strptime(date_iso, "%Y-%m-%d")
        weekday  = dt.weekday()   # 0=Mon … 5=Sat, 6=Sun
        is_sat   = weekday == 5
        is_sun   = weekday == 6

        # --- Helper closures ---
        def fb(*kws):
            return find_bullet(bullets, *kws)

        def desc_or(fallback, *kws):
            return fb(*kws) or fallback

        day_events = []  # list of (start_hhmm, end_hhmm, summary, description)

        # ── English block (common to all days) ──────────────────────────
        en_shadowing = desc_or("Shadowing + pronunciation drills.", "Shadowing", "shadowing")
        en_vocab     = desc_or("IT vocabulary study.", "Từ vựng", "voc")
        en_grammar   = desc_or("Grammar & sentence structures.", "Ngữ pháp", "grammar")
        en_desc      = f"1. {en_shadowing}\\n2. {en_vocab}\\n3. {en_grammar}"

        # ── Thứ 7 chẵn (nghỉ) — full day ───────────────────────────────
        if is_sat and (dt.day % 2 == 0):
            day_events += [
                ("04:30", "05:30", "🇬🇧 English Mastery",        en_desc),
                ("05:30", "07:00", "📚 Deep Topics Sprint",
                    desc_or("Đọc sâu tài liệu lớn, sách DDIA, thiết kế sơ đồ.", "Sprint", "Deep Topic", "System Design")),
                ("08:00", "10:00", "💻 LeetCode Marathon",
                    desc_or("90m timed: 3 bài liên tục + 30m review.", "LeetCode", "DSA", "Marathon")),
                ("10:00", "11:30", "🏗️ System Design",
                    desc_or("Tự giải 1 bài toán lớn, phác thảo API & Data Model.", "SD", "System Design")),
                ("11:30", "12:00", "📝 STAR Stories Practice",
                    desc_or("Viết & cập nhật 1-2 câu chuyện STAR.", "STAR", "star")),
                ("13:30", "15:30", "☕ Java/Spring Deep",
                    desc_or("Đọc sâu cơ chế phức tạp & Demo.", "Java", "Spring")),
                ("15:30", "17:00", "📄 CV & Apply",
                    desc_or("Tối ưu CV, viết cover letter, nộp đơn.", "CV", "Apply", "nộp")),
                ("17:00", "17:30", "📋 Weekly Review",
                    "Chấm điểm tiến độ tuần, cập nhật tracker."),
            ]

        # ── Thứ 7 lẻ (đi làm) ───────────────────────────────────────────
        elif is_sat and (dt.day % 2 == 1):
            day_events += [
                ("04:30", "05:30", "🇬🇧 English Mastery", en_desc),
                ("05:30", "06:30", "📚 Deep Topic",
                    desc_or("Đọc tài liệu lý thuyết sâu.", "Deep Topic", "System Design", "Research")),
                ("06:30", "07:00", "📝 Java/Spring Deep",
                    desc_or("Java/Spring deep study.", "Java", "Spring", "Active Recall")),
                ("20:00", "22:00", "💻 LeetCode Marathon",
                    desc_or("Timed coding cuối tuần.", "LeetCode", "DSA", "Marathon", "STAR")),
            ]

        # ── Chủ Nhật ────────────────────────────────────────────────────
        elif is_sun:
            has_sd_le   = fb("System Design (Lẻ)", "SD (Lẻ)")
            has_star_le = fb("STAR/CV Prep (Lẻ)", "STAR/CV (Lẻ)")

            day_events += [
                ("04:30", "05:30", "🇬🇧 English Mastery", en_desc),
                ("05:30", "07:00", "📚 Deep Topic / Active Recall",
                    desc_or("Active Recall từ Q&A cuối file. Giải thích to không nhìn tài liệu.",
                            "Active Recall", "Deep Topic", "Review")),
                ("09:00", "11:00", "📖 Reading / DDIA",
                    desc_or("Đọc DDIA hoặc tài liệu system-level.", "DDIA", "Reading")),
            ]

            if has_sd_le or has_star_le:
                day_events += [
                    ("11:00", "12:30", "🏗️ System Design (Lẻ)",
                        has_sd_le or "Bổ sung kiến thức thiết kế hệ thống."),
                    ("14:00", "16:00", "🎤 Mock Interview",
                        desc_or("Giả lập phỏng vấn & đánh giá khuyết điểm.", "Mock", "Full mock")),
                    ("16:00", "17:00", "📝 STAR/CV Prep (Lẻ)",
                        has_star_le or "Viết STAR stories & CV bằng tiếng Anh."),
                ]
            else:
                day_events += [
                    ("14:00", "16:00", "🎤 Mock Interview",
                        desc_or("Giả lập phỏng vấn & đánh giá khuyết điểm.", "Mock", "Full mock")),
                ]

            day_events.append(
                ("21:30", "22:00", "🗣️ Technical Speaking",
                    desc_or("Nói to giải thích concept vừa học.", "Speaking", "Nói to", "Record"))
            )

        # ── Weekday (T2 – T6) ────────────────────────────────────────────
        else:
            deep_topic = desc_or(
                "Đọc tài liệu lý thuyết sâu / Active Recall Q&A.",
                "Active Recall", "Deep Topic", "Research", "System Design"
            )
            java_topic = desc_or(
                "Java/Spring deep internals.",
                "Java", "Spring", "Sáng Java", "CV"
            )
            dsa_topic  = desc_or(
                "Vẽ thuật toán dry-run, code giải Easy/Medium.",
                "DSA", "LeetCode", "Tối DSA", "Practice"
            )
            speak_topic = desc_or(
                "Giải thích concept vừa học bằng tiếng Anh (Talk Out Loud).",
                "Speaking", "Nói to", "STAR", "Tối Speaking"
            )

            day_events += [
                ("04:30", "05:30", "🇬🇧 English Mastery",   en_desc),
                ("05:30", "06:30", "📚 Deep Topic",           deep_topic),
                ("06:30", "07:00", "📝 Java/Spring Deep",     java_topic),
                ("20:00", "21:30", "🧠 DSA Concept & Code",   dsa_topic),
                ("21:30", "22:00", "🗣️ Technical Speaking",  speak_topic),
            ]

        # ── Build ICS events ────────────────────────────────────────────
        date_compact = date_iso.replace("-", "")
        for start_hm, end_hm, summary, raw_desc in day_events:
            start_dt = f"{date_compact}T{start_hm.replace(':', '')}00"
            end_dt   = f"{date_compact}T{end_hm.replace(':', '')}00"
            uid      = f"comeback_{start_dt}_{re.sub(r'[^a-zA-Z0-9]', '', summary)[:12]}@antigravity"
            ics_events.append({
                "uid":     uid,
                "start":   start_dt,
                "end":     end_dt,
                "summary": summary,
                "desc":    clean_description(raw_desc),
            })

    # ── Write ICS ───────────────────────────────────────────────────────
    with open(ICS_PATH, "w", encoding="utf-8") as f:
        f.write("BEGIN:VCALENDAR\n")
        f.write("VERSION:2.0\n")
        f.write("PRODID:-//Antigravity Interview Comeback//VN\n")
        f.write("CALSCALE:GREGORIAN\n")
        f.write("METHOD:PUBLISH\n")
        f.write("X-WR-CALNAME:Interview Comeback Plan 2026\n")
        f.write("X-WR-TIMEZONE:Asia/Ho_Chi_Minh\n")

        for ev in ics_events:
            f.write("BEGIN:VEVENT\n")
            f.write(f"UID:{ev['uid']}\n")
            f.write(f"DTSTART:{ev['start']}\n")
            f.write(f"DTEND:{ev['end']}\n")
            f.write(f"SUMMARY:{ev['summary']}\n")
            f.write(f"DESCRIPTION:{ev['desc']}\n")
            f.write("BEGIN:VALARM\n")
            f.write("TRIGGER:-PT5M\n")
            f.write("ACTION:DISPLAY\n")
            f.write("DESCRIPTION:Reminder\n")
            f.write("END:VALARM\n")
            f.write("END:VEVENT\n")

        f.write("END:VCALENDAR\n")

    print(f"✅ Tạo lịch thành công: {len(ics_events)} events")
    print(f"   Nguồn: {MD_PATH}")
    print(f"   Xuất:  {ICS_PATH}")


if __name__ == "__main__":
    generate_ics()
