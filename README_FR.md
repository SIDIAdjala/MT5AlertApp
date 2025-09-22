# MT5 Alert App - Application Android d'Alertes Trading

Application Android complémentaire au MT5 Scanner pour recevoir des alertes de trading en temps réel sur votre smartphone.

## 🚀 Fonctionnalités

- **📡 Connexion temps réel** : WebSocket vers le scanner MT5
- **🔊 Alarmes sonores** : Sons personnalisables pour chaque alerte
- **🔔 Notifications push** : Notifications Android avec détails
- **📱 Fonctionnement arrière-plan** : Service persistant même app fermée
- **🔄 Auto-reconnexion** : Reconnexion automatique en cas de perte réseau
- **⚡ Démarrage auto** : Optionnel au boot de l'appareil
- **🔒 Authentification** : Token sécurisé pour la connexion
- **🔋 Optimisé batterie** : Wake lock intelligent pour économiser l'énergie

## 📋 Prérequis

### Système
- **Android 5.0+** (API 21 minimum)
- **Connexion Internet** (WiFi ou données mobiles)
- **Autorisations** : Notifications, foreground service

### Scanner MT5
- **MT5 Scanner** déployé et fonctionnel (dossier `MT5Scanner15`)
- **Serveur WebSocket** activé (port 8000)
- **URL d'accès** au scanner (ex: Replit URL)

## 🔧 Installation

### Option 1: APK Précompilée (Recommandée)
1. Télécharger l'APK depuis les releases
2. Activer "Sources inconnues" dans Android
3. Installer l'APK
4. Accorder les permissions demandées

### Option 2: Compilation depuis Source
1. **Android Studio** installé
2. **Cloner** le projet
3. **Ouvrir** le dossier `MT5AlertApp` dans Android Studio
4. **Build** → Generate Signed APK
5. **Installer** l'APK généré

## ⚙️ Configuration

### 1. Première Ouverture
1. **Lancer l'app** MT5 Alert
2. **Accorder les permissions** :
   - Notifications (obligatoire)
   - Ignorar optimisation batterie (recommandé)
3. **Voir l'écran de configuration**

### 2. Configuration WebSocket

#### URL du Scanner
- **Format** : `wss://votre-domaine.com?token=mt5_scanner_2024`
- **Replit** : `wss://votre-username-projectname.replit.dev?token=mt5_scanner_2024`
- **URL par défaut** : Préconfigurée avec le domaine actuel

#### Token d'Authentification
- **Token par défaut** : `mt5_scanner_2024`
- **Sécurité** : Changez le token dans le scanner ET l'app si besoin
- **Format URL** : Le token est inclus dans l'URL comme paramètre

### 3. Son d'Alerte
1. **Cliquer** sur "Choisir Son d'Alerte"
2. **Sélectionner** une sonnerie dans la liste Android
3. **Tester** le son choisi
4. **Sauvegarder** automatiquement

## 📱 Utilisation

### Démarrage du Service
1. **Configurer l'URL** WebSocket du scanner
2. **Cliquer** "Démarrer Service"
3. **Vérifier** le statut "Service Running"
4. **Notification persistante** apparaît (normal)

### Réception d'Alertes
- **Son d'alarme** joué automatiquement
- **Notification push** avec détails de l'alerte
- **Réveil de l'écran** si en veille
- **Vibration** selon paramètres Android

### Arrêt du Service
1. **Cliquer** "Arrêter Service"
2. **Notification** disparaît
3. **Plus d'alertes** reçues
4. **Reconnexion manuelle** nécessaire

## 🔧 Paramètres Avancés

### Configuration WebSocket
```kotlin
// Dans MainActivity.kt - modifier si nécessaire
private const val DEFAULT_WEBSOCKET_URL = "wss://votre-domaine.replit.dev?token=mt5_scanner_2024"
```

### Paramètres de Reconnexion
```kotlin
// Dans WebSocketForegroundService.kt
private const val MAX_RECONNECT_ATTEMPTS = 10
private const val RECONNECT_DELAY_MS = 5000L
```

### Notifications
- **Canal** : "WebSocket Connection"
- **Priorité** : Haute pour les alertes
- **Son** : Personnalisable
- **Vibration** : Configurable dans Android

## 📊 Interface Utilisateur

### Écran Principal
- **URL WebSocket** : Champ de saisie modifiable
- **Son sélectionné** : Affichage du son choisi
- **Boutons de contrôle** : Start/Stop service
- **Statut connexion** : Indication en temps réel

### Notifications
- **Service persistant** : "MT5 Alert Service - Connected"
- **Alertes trading** : "MT5 Alert! - Détails de l'alerte"
- **Erreurs connexion** : "Connection failed - Reconnecting..."

## 🚨 Résolution de Problèmes

### L'app ne se connecte pas
1. **Vérifier l'URL** WebSocket (commence par `wss://`)
2. **Tester le scanner** dans un navigateur
3. **Vérifier le token** d'authentification
4. **Contrôler la connexion** Internet

### Pas de son d'alerte
1. **Volume du téléphone** non muet
2. **Son sélectionné** valide dans l'app
3. **Mode "Ne pas déranger"** désactivé
4. **Permissions audio** accordées

### Service s'arrête tout seul
1. **Désactiver optimisation batterie** pour l'app
2. **Autoriser** en arrière-plan dans Android
3. **Mettre en liste blanche** dans l'antivirus
4. **Redémarrer** l'appareil si besoin

### Reconnexions fréquentes
1. **Stabilité réseau** : Vérifier WiFi/4G
2. **Scanner online** : Vérifier que le serveur fonctionne
3. **Réduire intervalle** de reconnexion
4. **Changer de réseau** (WiFi ↔ 4G)

## 🔋 Optimisation Batterie

### Wake Lock Intelligent
- **Activation automatique** pendant connexion WebSocket
- **Libération** quand service arrêté
- **Durée limitée** (10 minutes par acquisition)

### Paramètres Android
1. **Paramètres** → Apps → MT5 Alert
2. **Batterie** → Optimisation batterie
3. **Désactiver** l'optimisation pour cette app
4. **Autoriser** activité en arrière-plan

## 📋 Format des Messages d'Alerte

### Message JSON Reçu
```json
{
  "type": "alert",
  "timestamp": "2025-09-20T10:30:00.000Z",
  "server_time": "2025-09-20 10:30:00",
  "symbol": "EURUSD",
  "price": 1.0950,
  "conditions": ["Zone: Support Level", "MFI > 70"],
  "message": "🚨 Alert for EURUSD\nPrice: 1.0950\nConditions: Zone: Support Level, MFI > 70\nTime: 10:30:00"
}
```

### Affichage Notification
- **Titre** : "MT5 Alert!"
- **Contenu** : Message formaté du scanner
- **Action** : Ouvre l'app au clic

## 🔗 Connexion avec le Scanner

### Architecture
```
[MT5 Scanner] → WebSocket Server (port 8000) → [Android App]
     ↓                    ↓                          ↓
Configuration      Authentification            Notifications
   Scanner            par Token                  Sonores
```

### Flux de Données
1. **Scanner** détecte condition (zone + MFI)
2. **Serveur WebSocket** diffuse l'alerte JSON
3. **App Android** reçoit et traite le message
4. **Son + notification** déclenchés automatiquement

## 🛠️ Développement

### Structure du Projet
```
MT5AlertApp/
├── app/src/main/java/com/mt5alert/app/
│   ├── MainActivity.kt              # Interface utilisateur
│   ├── WebSocketForegroundService.kt # Service de connexion
│   └── BootReceiver.kt             # Démarrage auto (optionnel)
├── app/src/main/res/
│   ├── layout/activity_main.xml    # Interface XML
│   ├── drawable/                   # Icônes
│   └── values/                     # Strings, couleurs
└── app/build.gradle                # Dépendances
```

### Dépendances Principales
```gradle
implementation 'com.squareup.okhttp3:okhttp:4.x.x'
implementation 'androidx.core:core-ktx:1.x.x'
implementation 'androidx.appcompat:appcompat:1.x.x'
```

### Permissions Requises
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

## 📈 Statistiques et Monitoring

### Métriques App
- **Connexions** : Total et actives
- **Alertes reçues** : Compteur
- **Reconnexions** : Fréquence
- **Uptime** : Temps de fonctionnement

### Logs de Debug
- **Service lifecycle** : Start/Stop/Reconnection
- **WebSocket events** : Open/Close/Message/Error
- **Audio playback** : Sound selection et playback

## 🔮 Fonctionnalités Futures

### Version 2.0
- [ ] **Historique des alertes** reçues
- [ ] **Statistiques détaillées** (graphiques)
- [ ] **Sons personnalisés** (import MP3)
- [ ] **Widgets Android** pour statut
- [ ] **Mode silencieux** programmable

### Intégrations
- [ ] **WhatsApp** notifications
- [ ] **Slack** integration
- [ ] **Discord** webhooks
- [ ] **Email forwarding**

## ⚠️ Avertissements Importants

### Trading
- **Informations uniquement** : Les alertes sont indicatives
- **Aucune garantie** de profits ou de performance
- **Responsabilité utilisateur** : Décisions de trading personnelles
- **Test obligatoire** : Toujours tester avant argent réel

### Technique
- **Connexion requise** : Internet permanent nécessaire
- **Batterie** : Consommation modérée en arrière-plan
- **Données mobiles** : Usage minimal (messages texte uniquement)
- **Compatibilité** : Android 5.0+ testé jusqu'à Android 14

## 📧 Support

### Logs de Debug
1. **Activer** Developer Options dans Android
2. **Connecter** en USB et utiliser `adb logcat`
3. **Filtrer** : `adb logcat | grep MT5Alert`

### Problèmes Connus
- **Huawei/Xiaomi** : Paramètres d'économie d'énergie agressifs
- **Android 12+** : Restrictions renforcées background
- **Émulateurs** : Problèmes audio possibles

---

**Version** : 1.0  
**Plateforme** : Android 5.0+ (API 21-34)  
**Licence** : Usage personnel et éducatif  
**Développé** : Kotlin + OkHttp WebSocket