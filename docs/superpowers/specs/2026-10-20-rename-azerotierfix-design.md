# 项目改名设计：ZerotierFix → aZerotierFix

日期：2026-10-20

## 目标

将项目名称从 ZerotierFix 改为 aZerotierFix，GitHub 仓库路径从 `zivyuan/ZerotierFix` 改为 `zivyuan/a-zerotier-fix`。

## 已确认决策

| 决策点 | 结论 |
|---|---|
| Java 包名 / applicationId | 全部改为 `net.kaaass.azerotierfix` |
| 应用显示名 | `aZerotierFix` |
| Application 类名 | 改为 `AZerotierFixApplication`（Java 类名首字母大写规范） |
| 老用户数据 | 不迁移（改 applicationId 后 Android 视为全新应用，已确认接受） |
| GitHub 仓库改名 | 由 Claude 用 `gh repo rename` 执行 |
| JNI | core 模块绑定的是 `com.zerotier.sdk`（上游 SDK 包），不受影响，不动 |

## 改动清单（按执行顺序）

1. **GitHub 仓库改名**
   - `gh repo rename a-zerotier-fix`（旧 URL 由 GitHub 自动重定向）
   - 本地 remote 更新为 `git@github.com:zivyuan/a-zerotier-fix.git`

2. **包名迁移** `net.kaaass.zerotierfix` → `net.kaaass.azerotierfix`
   - `git mv` 迁移 `app/src/main/java/net/kaaass/zerotierfix/`、`app/src/test/...`、`app/src/androidTest/...` 三处目录（保留 git 历史）
   - 全局文本替换包名引用：import 语句、AndroidManifest（application/service 名）、app/build.gradle（applicationId、namespace、daoPackage）、intent action 等字符串字面量
   - 顺带替换 AndroidManifest 中 application 类名引用

3. **名称更新**
   - `settings.gradle`：`rootProject.name = "aZerotierFix"`
   - `app/src/main/res/values/strings.xml`：`app_name` → `aZerotierFix`
   - 主题样式 `Theme.ZerotierFix` → `Theme.aZerotierFix`（values/themes.xml、values-night/themes.xml、AndroidManifest、layout 引用）
   - 类重命名：`ZerotierFixApplication.java` → `AZerotierFixApplication.java`，类名与所有引用同步

4. **README.md**
   - 所有 `zivyuan/ZerotierFix` 链接（badge、Releases、Actions）→ `zivyuan/a-zerotier-fix`
   - 正文中 `net.kaaass.zerotierfix` 的说明同步更新

## 不改动

- core 模块（`com.zerotier.sdk` JNI 绑定、native 代码）
- `.github/workflows/build-app.yml`（无项目名引用，已核实）
- externals、数据库结构、SharedPreferences 逻辑

## 验证

- `./gradlew assembleDebug` 构建通过
- `grep -r 'net.kaaass.zerotierfix\|ZerotierFix' --exclude-dir=.git --exclude-dir=build` 无残留（历史文档除外，逐条人工确认）

## 提交

- 仓库内改动一个 commit，中文注释，不加署名尾注
- 仓库改名先行执行（旧 URL 有重定向，中间态不破坏链接）
