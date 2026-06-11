# 📖 Git - Lý Thuyết & Interview

> *Nội dung lý thuyết Git được gộp chung vào [13-Build-Tools/Theory.md](../13-Build-Tools/Theory.md)*

---

## Tóm Tắt Nhanh Git

### Core Workflow
```bash
git clone <repo>          # Clone repo
git checkout -b feature   # Tạo branch mới
# ... code ...
git add .                 # Stage changes
git commit -m "message"   # Commit
git push origin feature   # Push to remote
# Create Pull Request trên GitHub
```

### Most-Used Commands
```bash
git status                    # Check trạng thái
git log --oneline -10         # 10 commits gần nhất
git diff                      # Xem thay đổi chưa stage
git stash                     # Lưu tạm thay đổi
git rebase -i HEAD~3          # Interactive rebase
git cherry-pick <hash>        # Lấy commit cụ thể
git reset HEAD~1              # Undo last commit
git revert <hash>             # Safe undo (tạo commit mới)
```

---

## Interview Q&A

### Q1: git merge vs git rebase?
- **merge**: Tạo merge commit, preserve history → dùng cho public branches
- **rebase**: Rewrite history, linear → dùng cho local/feature branches trước khi merge

### Q2: git pull vs git fetch?
- **fetch**: Download only, không merge → safe
- **pull**: fetch + merge → có thể gây conflict

### Q3: Làm sao undo commit đã push?
```bash
git revert <commit>    # Safe: tạo commit mới đảo ngược
git push origin main
# KHÔNG dùng git reset --hard rồi push --force trên shared branches
```

### Q4: .gitignore dùng để làm gì?
File khai báo patterns của files/directories Git sẽ KHÔNG track.

```gitignore
# .gitignore cho Java project
target/
*.class
*.jar
.DS_Store
.idea/
*.log
application-local.properties
```

### Q5: Git workflow phổ biến?

**Git Flow:**
- `main`: production
- `develop`: integration
- `feature/*`, `bugfix/*`, `release/*`, `hotfix/*`

**GitHub Flow** (simpler):
- `main`: always deployable
- `feature/*`: branch off main → PR → merge to main

---

*📌 Xem chi tiết: [13-Build-Tools/Theory.md](../13-Build-Tools/Theory.md)*
