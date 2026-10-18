# Peso

Aplicativo Android para registrar pesagens e acompanhar sua evolução ao longo do tempo.

O Peso reúne histórico, variação desde o primeiro registro, gráfico de evolução e uma meta opcional em uma experiência simples e inteiramente offline. Cada pesagem guarda peso e data, permitindo corrigir registros e observar a trajetória sem criar uma conta ou enviar informações para um servidor.

[Baixar a versão mais recente](https://github.com/dhianapereira/peso/releases/latest)

## Funcionalidades

- Registro de peso com data escolhida pela pessoa.
- Histórico com peso, variação em relação à pesagem anterior e data.
- Gráfico de evolução pela média de peso de cada mês.
- Ação de exclusão à esquerda e edição à direita do registro, ambas com confirmação.
- Histórico ordenado das pesagens.
- Cálculo da diferença entre o peso atual e o primeiro registro.
- Meta de peso opcional e indicação da distância até ela.
- Temas claro, escuro ou definido pelo sistema.
- Interface em português e inglês.
- Exportação do histórico e da meta para um arquivo JSON.
- Restauração completa a partir de um backup, com confirmação antes de substituir os dados.
- Configurações com preferências, dados e backup, informações legais e sobre o app.
- Funcionamento inteiramente offline, sem conta ou servidor externo.

## Privacidade

As pesagens, a meta e as preferências ficam armazenadas localmente no dispositivo. O Peso não possui acesso à internet, não exibe anúncios e não utiliza serviços de análise ou rastreamento.

Os arquivos exportados são salvos no destino escolhido pela pessoa. A restauração substitui o histórico e a meta atuais após confirmação. Backups JSON não são criptografados pelo app.

## Tecnologias

- Kotlin e Jetpack Compose.
- Material 3.
- Room para persistência das pesagens.
- DataStore para preferências e meta.
- Hilt para injeção de dependência.
- AppCompat para seleção de idioma.
- Coroutines e Flow.
- Arquitetura MVVM organizada por funcionalidade.
- ktlint para formatação e verificação do código Kotlin.

## Como executar

### Pré-requisitos

- Android Studio com JDK 17 ou superior.
- Android SDK 35 instalado.
- Emulador ou dispositivo com Android API 26 ou superior.

### Android Studio

1. Abra a raiz do projeto no Android Studio.
2. Aguarde a sincronização do Gradle.
3. Selecione o módulo `app` e um dispositivo ou emulador.
4. Clique em **Run**.

O Android Studio cria o `local.properties` automaticamente com o caminho do SDK local.

### Terminal

Use o Gradle Wrapper incluído no projeto:

```bash
# Formatar o código Kotlin
./gradlew ktlintFormat

# Verificar a formatação
./gradlew ktlintCheck

# Gerar o APK de debug
./gradlew assembleDebug

# Instalar em um dispositivo conectado
./gradlew installDebug

# Executar os testes unitários
./gradlew testDebugUnitTest

# Executar o Android Lint
./gradlew lintDebug
```

No Windows, substitua `./gradlew` por `gradlew.bat`.

## Estrutura do projeto

O projeto utiliza MVVM e organiza persistência e interface por responsabilidade:

- `model/`: regras de cálculo, tema e formatação dos pesos.
- `data/weight/`: entidade, DAO e repositório das pesagens.
- `data/backup/`: exportação, validação e restauração dos backups JSON.
- `data/database/`: configuração do banco de dados Room.
- `data/preferences/`: preferências e meta persistidas com DataStore.
- `di/`: módulos de injeção de dependência com Hilt.
- `ui/components/`: componentes compartilhados entre as telas.
- `ui/entry/`: formulários de pesagem e meta.
- `ui/history/`: histórico de pesagens.
- `ui/overview/`: resumo, métricas e gráfico de evolução.
- `ui/theme/`: cores e temas do Jetpack Compose.
- `ui/Settings.kt`: preferências, backup, informações legais e sobre o app.

As traduções ficam em `app/src/main/res/values/` e `app/src/main/res/values-en/`. Os schemas do Room ficam em `app/schemas/`.

## Releases

As releases incluem o APK para instalação em dispositivos Android.

- [Baixar a versão mais recente](https://github.com/dhianapereira/peso/releases/latest)

## Licença

O código-fonte está licenciado sob a [Licença MIT](./LICENSE).

O logotipo, os ícones, as capturas de tela e os elementos de identidade visual não estão cobertos pela Licença MIT e permanecem sob a condição de Todos os direitos reservados.
