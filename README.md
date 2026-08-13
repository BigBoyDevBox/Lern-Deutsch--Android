<div align="center">

# 🐱 Wortkatze · 词猫

**德语和英语单词，用玩的方式记住。**
**Deutsche und englische Wörter — spielend gelernt.**

[![CI](https://github.com/L-K-M/Lern-Deutsch--Android/actions/workflows/ci.yml/badge.svg)](https://github.com/L-K-M/Lern-Deutsch--Android/actions/workflows/ci.yml)

</div>

Wortkatze 是给中文母语学习者做的**德语**学习 app —— 也顺便练**英语**。
它是 [Pebble 手表版 *Lern Deutsch*](https://github.com/L-K-M/Lern-Deutsch--Pebble)
的大屏幕续集：同一份 **1204 个单词**的词库，更多颜色、更多动画、更多小猫。

Wortkatze ist eine **Deutsch**-Lern-App für Lernende mit chinesischer
Muttersprache — und nebenbei auch **Englisch**. Sie ist die grosse Schwester
der [Pebble-Uhren-App *Lern Deutsch*](https://github.com/L-K-M/Lern-Deutsch--Pebble):
derselbe Wortschatz aus **1204 Wörtern**, aber mit mehr Farben, mehr
Animationen und mehr Katze.

> [!IMPORTANT]
> LLM 声明：本项目几乎全部由 AI 编程助手开发，包括代码审查。
> **LLM disclosure:** this app is developed almost entirely by LLM agents,
> including its reviews. See [AGENTS.md](AGENTS.md) for the operational
> conventions and [PLAN.md](PLAN.md) for the design.

## 五种玩法 · Fünf Spielarten

| 玩法 · Modus | 学什么 · Was du übst |
|---|---|
| **Karten** 卡片 | 翻卡片、自己打分 —— 经典单词卡 · Klassische Lernkarten zum Umdrehen |
| **Der Die Das** 冠词 | 名词的性别：der 蓝 · die 粉 · das 绿 · Die Artikel, farbcodiert |
| **Wortarten** 词性 | 名词、动词、形容词 · Nomen, Verben, Adjektive |
| **Quiz** 四选一 | 德⇄中、德⇄英，四个选项 · Multiple Choice, auch auf Englisch |
| **Blitz** 闪电 | 60 秒抢答，连击加倍 · 60 Sekunden, Combo-Multiplikator |

每天至少玩一轮，主页的小火焰 🔥 就会数你连续学习的天数。答对了会有彩纸爆炸 🎉，
答错了小猫会陪你再试一次。
Spiel jeden Tag mindestens eine Runde — die Flamme 🔥 zählt deine Tage-Serie.
Richtige Antworten explodieren in Konfetti 🎉; bei einer falschen probiert die
Katze es einfach nochmals mit dir.

## 小提示 · Gut zu wissen

德语名词要连冠词一起记。app 里冠词永远有颜色 —— 和手表版一样：
Deutsche Nomen lernt man am besten zusammen mit dem Artikel. Die Farben sind
dieselben wie auf der Uhr:

* **der**（阳性）= 蓝色 blau · **die**（阴性）= 粉色 rosa · **das**（中性）= 绿色 grün

拼写用瑞士写法：永远写 *ss*，不写 *ß*。
Die Schreibweise ist schweizerisch: immer *ss*, nie *ß*.

## 安装 · Installation

从 **Releases** 页下载最新的 `.apk`，传到手机上打开即可（需要允许「安装未知来源应用」）。
Lade die neuste `.apk` von der **Releases**-Seite, kopiere sie aufs Handy und
öffne sie (Installation aus unbekannter Quelle erlauben).

需要 Android 8.0（API 26）或更新的系统。
Android 8.0 (API 26) oder neuer.

## Building

```sh
./gradlew testDebugUnitTest lintDebug assembleDebug   # what CI runs
scripts/build.sh                                      # release APK -> dist/
scripts/install.sh                                    # build + install + launch
python3 tools/gen_vocab.py                            # after editing the word list
```

Requirements: JDK 17, Android SDK (set `sdk.dir` in `local.properties` — see
`local.properties.example`), Python 3 for the vocabulary tools. Both build
types are signed with the checked-in debug keystore so any clone produces
installable, upgrade-compatible APKs (a deliberate sideload-only decision —
see [`docs/decisions/0002`](docs/decisions/0002-zero-secret-signing.md)).

## Releasing

`scripts/release.sh X.Y.Z --push` — never hand-edit `versionCode`, never create
a `v*` tag by hand. CI publishes the APK to GitHub Releases.

## License

[Unlicense](LICENSE) — public domain.

祝你学习顺利！加油！Viel Erfolg beim Deutschlernen! 💛
