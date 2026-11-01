# Processo de release

As releases do Peso são criadas automaticamente quando uma tag no formato
`vMAJOR.MINOR.PATCH` é enviada ao GitHub. O workflow valida o projeto, gera APK
e Android App Bundle (AAB) assinados, calcula checksums SHA-256 e publica uma
GitHub Release com as notas geradas pelo GitHub.

## Configuração inicial

Crie e guarde a chave de assinatura fora do repositório:

```bash
keytool -genkeypair -v \
  -keystore peso-release.jks \
  -alias peso \
  -keyalg RSA \
  -keysize 4096 \
  -validity 10000
```

Faça backups seguros do arquivo, alias e senhas. A chave será necessária para
assinar todas as futuras atualizações do app.

Converta o keystore para Base64 em uma única linha:

```bash
base64 < peso-release.jks | tr -d '\n'
```

Em **Settings → Secrets and variables → Actions**, cadastre os Repository
Secrets abaixo:

| Secret                      | Conteúdo                    |
| --------------------------- | --------------------------- |
| `RELEASE_KEYSTORE_BASE64`   | Resultado do comando Base64 |
| `RELEASE_KEYSTORE_PASSWORD` | Senha do keystore           |
| `RELEASE_KEY_ALIAS`         | Alias da chave (`peso`)      |
| `RELEASE_KEY_PASSWORD`      | Senha da chave              |

## Gerar a primeira versão

Com as mudanças aprovadas e presentes na branch `main`, crie e envie a tag:

```bash
git switch main
git pull --ff-only
git tag -a v1.0.0 -m "Release v1.0.0"
git push origin v1.0.0
```

Acompanhe **Actions → Android Release**. Ao concluir, a release conterá
`peso-1.0.0.apk`, `peso-1.0.0.aab` e `SHA256SUMS.txt`.

A tag define automaticamente a versão Android. Por exemplo, `v1.2.3` produz
`versionName=1.2.3` e `versionCode=1002003`.

Se o workflow falhar depois que a tag já tiver sido publicada, corrija o
problema em um novo commit e use uma nova versão. Não mova uma tag publicada.
