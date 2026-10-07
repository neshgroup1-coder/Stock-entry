# Stock Entry

HTML app (Supabase database) -> GitHub Pages website + Android APK.

## 1. GitHub-il upload cheyyam
1. github.com -> New repository -> name: `stock-entry` -> Create.
2. "uploading an existing file" click cheyth ee folderile ella files-um (www, assets, .github, package.json, capacitor.config.json, .gitignore) upload cheyyuka -> Commit.
   - `.github` folder upload aayillenkil: Add file -> Create new file -> name-il `.github/workflows/build-apk.yml` ennu type cheyth content paste cheyyuka (pages.yml-num athupole).

## 2. Website (GitHub Pages)
Settings -> Pages -> Source: **GitHub Actions**.
Oru minute kazhinj link kittum: `https://<username>.github.io/stock-entry/`
(Phone Chrome-il open cheyth "Add to Home screen" koduthalum app pole install aakum.)

## 3. APK
Repo -> **Actions** tab -> "Build Android APK" -> run complete aakumbol (~5-8 min) -> thazhe **Artifacts** -> `StockEntry-apk` download -> zip-il `app-debug.apk`.
Phone-il install cheyyumbol "Install unknown apps" allow cheyyanam.
Run cheyyan thonniyal: Actions -> Build Android APK -> Run workflow.

## Phone-il update varunnath
`www/index.html` commit cheythal: APK build aakum -> Releases-il `StockEntry.apk` maarum -> website update aakum. Install cheytha phones-il app thurakkumbol "New version available" button kaanum. Tap cheythal app-ilthanne APK download aakum, pinne Android-nte Install/Update window varum (Chrome venda). Aadyam "Install unknown apps" allow cheyyan paranjal allow cheyth thirich vannu veendum tap cheyyuka.
Fixed signing key (`debug.keystore`) ullathu kondu update pazhaya app-inu mukalil install aakum, data poyilla.
Pazhaya APK (ee key illathe build cheythath) onnu **uninstall** cheyth puthiyathu install cheyyanam. Athinu munpe app-il "not synced" entries illennu urappikkuka.

## App maattiyal
`www/index.html` edit cheyth commit cheyyuka. Website-um APK-yum automatic aayi puthuthaakum.

## Security
`www/index.html`-il Supabase URL + publishable key undu. Repo public aanenkil aarkum athu kaanam, anon insert allowed aayathu kondu aarkum entries ezhuthan pattum. Repo **Private** aakki vekkunnathaanu nallath (Private repo-il APK build free aanu; GitHub Pages-nu paid plan venam).
