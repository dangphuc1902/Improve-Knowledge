import re
from datetime import datetime
import os

def clean_description(desc):
    if not desc:
        return ""
    # Remove markdown link formatting [text](url) -> text (url)
    desc = re.sub(r'\[([^\]]+)\]\(([^)]+)\)', r'\1 (\2)', desc)
    # Remove bold/italic markup
    desc = desc.replace("**", "").replace("*", "").replace("`", "")
    # Escape special characters for ICS format
    desc = desc.replace("\\", "\\\\").replace(";", "\\;").replace(",", "\\,")
    desc = desc.replace("\n", "\\n")
    return desc

def generate_ics():
    md_path = "d:/WorkSpace/Document/Improve-Knowledge/Plan/01-detailed-sessions.md"
    ics_path = "d:/WorkSpace/Document/Improve-Knowledge/Plan/study_schedule.ics"
    
    if not os.path.exists(md_path):
        print(f"Error: {md_path} not found.")
        return

    with open(md_path, "r", encoding="utf-8") as f:
        content = f.read()

    # Parse days
    day_header_pat = re.compile(r"^####\s+(Thứ\s+\d+|Chủ\s+Nhật)\s+\((\d{2})/(\d{2})\)", re.MULTILINE)
    
    days = []
    matches = list(day_header_pat.finditer(content))
    
    for i, match in enumerate(matches):
        day_name = match.group(1)
        day_str = match.group(2)
        month_str = match.group(3)
        date_str = f"2026-{month_str}-{day_str}"
        
        # Get content between this header and the next one (or end of file/details block)
        start_idx = match.end()
        end_idx = matches[i+1].start() if i + 1 < len(matches) else len(content)
        day_content = content[start_idx:end_idx]
        
        bullets = []
        for line in day_content.splitlines():
            line_str = line.strip()
            if line_str.startswith("*") or line_str.startswith("-"):
                bullets.append(line_str)
                
        days.append({
            "date": date_str,
            "name": day_name,
            "bullets": bullets
        })

    ics_events = []
    
    for day in days:
        date_str = day["date"]
        bullets = day["bullets"]
        
        def find_bullet(keyword):
            for b in bullets:
                # remove bullet point symbol
                cleaned = re.sub(r'^[*+-]\s+', '', b)
                if keyword.lower() in cleaned.lower():
                    return cleaned
            return None
            
        dt = datetime.strptime(date_str, "%Y-%m-%d")
        is_sat = dt.weekday() == 5
        is_sun = dt.weekday() == 6
        
        day_events = []
        
        # Pull English contents for mornings
        en_vocab = find_bullet("Từ vựng") or "IT vocabulary drills."
        en_grammar = find_bullet("Ngữ pháp") or "Grammar & sentence structures."
        en_shadowing = find_bullet("Shadowing") or "Speaking & Pronunciation shadowing."
        en_morning_desc = f"1. {en_shadowing}\\n2. {en_vocab}\\n3. {en_grammar}"
        
        if is_sat:
            day_num = dt.day
            is_even = (day_num % 2 == 0)
            
            if is_even:
                # Saturday Even (Rest Day)
                day_events.append(("04:30", "05:30", "🇬🇧 English Mastery", en_morning_desc))
                day_events.append(("05:30", "07:00", "📚 Deep Topics Sprint", find_bullet("Sprint") or find_bullet("Deep") or "Đọc sâu tài liệu lớn, sách DDIA, thiết kế sơ đồ."))
                day_events.append(("08:00", "10:00", "💻 LeetCode Marathon", "90m timed block giải 3 bài liên tục để luyện sức bền + 30m review."))
                day_events.append(("10:00", "11:30", "🏗️ System Design", find_bullet("SD") or find_bullet("System Design") or "Tự giải 1 bài toán lớn, phác thảo API & Data Model."))
                day_events.append(("11:30", "12:00", "📝 STAR Stories Practice", find_bullet("STAR") or "Viết & cập nhật 1-2 câu chuyện dự án theo khung STAR."))
                day_events.append(("13:30", "15:30", "☕ Java/Spring Deep", find_bullet("Java") or find_bullet("Spring") or "Đọc sâu cơ chế phức tạp & Demo."))
                day_events.append(("15:30", "17:00", "📄 CV & Apply prep", find_bullet("CV") or "Tối ưu CV, viết cover letter, nộp đơn."))
                day_events.append(("17:00", "17:30", "📋 Weekly Review", "Chấm điểm tiến độ tuần."))
            else:
                # Saturday Odd (Workday)
                day_events.append(("04:30", "05:30", "🇬🇧 English Mastery", en_morning_desc))
                day_events.append(("05:30", "06:30", "📚 Deep Topic", find_bullet("Deep") or "Đọc tài liệu lý thuyết sâu."))
                day_events.append(("06:30", "07:00", "📝 Java/Spring Deep", find_bullet("Java") or find_bullet("Spring") or "Java/Spring deep study."))
                day_events.append(("20:00", "22:00", "💻 LeetCode Marathon", find_bullet("DSA") or find_bullet("Leetcode") or "Timed coding giải quyết các bài tập ôn luyện cuối tuần."))
                
        elif is_sun:
            has_sd_le = find_bullet("SD (Lẻ)") or find_bullet("System Design (Lẻ)")
            has_star_le = find_bullet("STAR/CV Prep (Lẻ)") or find_bullet("STAR/CV (Lẻ)")
            
            day_events.append(("04:30", "05:30", "🇬🇧 English Mastery", find_bullet("post") or "Viết 1 bài post ngắn chia sẻ kỹ thuật bằng tiếng Anh lên LinkedIn/GitHub."))
            day_events.append(("05:30", "07:00", "💻 LeetCode Review & Optimize", find_bullet("Review") or "Giải lại các bài bị stuck hoặc giải chậm, tối ưu code."))
            day_events.append(("09:00", "11:00", "📖 Reading (DDIA)", find_bullet("DDIA") or "Đọc 1 chương trong sách Designing Data-Intensive Applications."))
            
            if has_sd_le or has_star_le:
                day_events.append(("11:00", "12:30", "📚 System Design (Lẻ)", has_sd_le or "Bổ sung kiến thức thiết kế hệ thống."))
                day_events.append(("14:00", "16:00", "🎤 Mock Interview", find_bullet("Mock") or "Giả lập phỏng vấn System Design/Coding & Đánh giá khuyết điểm."))
                day_events.append(("16:00", "17:00", "📝 STAR/CV Prep (Lẻ)", has_star_le or "Viết STAR stories & CV."))
            else:
                day_events.append(("14:00", "16:00", "🎤 Mock Interview", find_bullet("Mock") or "Giả lập phỏng vấn System Design/Coding & Đánh giá khuyết điểm."))
                
            day_events.append(("17:00", "18:00", "🧘 Rest & Recharge", "Ngắt kết nối hoàn toàn, nghỉ ngơi lấy lại năng lượng."))
            
        else:
            # Weekday (Mon-Fri)
            # Check if has split morning
            has_split_morning = any("java" in b.lower() or "spring" in b.lower() or "prep" in b.lower() for b in bullets if "deep" not in b.lower())
            
            day_events.append(("04:30", "05:30", "🇬🇧 English Mastery", en_morning_desc))
            
            if has_split_morning:
                day_events.append(("05:30", "06:30", "📚 Deep Topic", find_bullet("Deep") or "Đọc tài liệu lý thuyết sâu."))
                day_events.append(("06:30", "07:00", "📝 Java/Spring Deep", find_bullet("Java") or find_bullet("Spring") or find_bullet("Prep") or "Đọc internals của Java Core/Spring."))
            else:
                day_events.append(("05:30", "07:00", "📚 Deep Topic (System Design / Company)", find_bullet("Deep") or "Deep study block."))
                
            # Evening
            has_split_evening = any("speaking" in b.lower() or "talk out loud" in b.lower() for b in bullets if "dsa" not in b.lower())
            if has_split_evening:
                day_events.append(("20:00", "21:30", "🧠 DSA Concept & Code", find_bullet("DSA") or "Vẽ thuật toán, code giải Easy/Medium."))
                day_events.append(("21:30", "22:00", "🗣️ Technical Speaking", find_bullet("Speaking") or "Giải thích giải pháp DSA hoặc cấu trúc bằng tiếng Anh."))
            else:
                day_events.append(("20:00", "22:00", "🧠 DSA / LeetCode Maintenance", find_bullet("DSA") or find_bullet("Leetcode") or "Thực hành giải bài Leetcode."))
                
        # Format events into ICS list
        for start_time, end_time, summary, desc in day_events:
            start_dt = f"{date_str.replace('-', '')}T{start_time.replace(':', '')}00"
            end_dt = f"{date_str.replace('-', '')}T{end_time.replace(':', '')}00"
            uid = f"study_{start_dt}_{summary.replace(' ', '_')[:10]}@antigravity"
            
            ics_events.append({
                "uid": uid,
                "start": start_dt,
                "end": end_dt,
                "summary": summary,
                "desc": clean_description(desc)
            })

    # Write ICS file
    with open(ics_path, "w", encoding="utf-8") as f:
        f.write("BEGIN:VCALENDAR\n")
        f.write("VERSION:2.0\n")
        f.write("PRODID:-//Antigravity Study Plan//VN\n")
        f.write("CALSCALE:GREGORIAN\n")
        f.write("METHOD:PUBLISH\n")
        
        for ev in ics_events:
            f.write("BEGIN:VEVENT\n")
            f.write(f"UID:{ev['uid']}\n")
            f.write(f"DTSTART:{ev['start']}\n")
            f.write(f"DTEND:{ev['end']}\n")
            f.write(f"SUMMARY:{ev['summary']}\n")
            f.write(f"DESCRIPTION:{ev['desc']}\n")
            # 5-minute reminder notification alarm
            f.write("BEGIN:VALARM\n")
            f.write("TRIGGER:-PT5M\n")
            f.write("ACTION:DISPLAY\n")
            f.write("DESCRIPTION:Reminder\n")
            f.write("END:VALARM\n")
            f.write("END:VEVENT\n")
            
        f.write("END:VCALENDAR\n")
        
    print(f"Successfully generated {len(ics_events)} events in {ics_path}")

if __name__ == "__main__":
    generate_ics()
