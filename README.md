📱 Application mobile – Gestion des pharmacies (ERP Odoo)

Cette application mobile a été développée dans le cadre d’un projet ERP, dont l’objectif principal était la conception et l’implémentation d’un module Odoo dédié à la gestion des pharmacies de proximité et de permanence.

🔗 Module Odoo (backend) : https://github.com/ImaneAouttahe/odoo-pharmacie.git

En complément du développement du module Odoo et de l’interface web accessible aux utilisateurs, une version mobile Android a été réalisée en Java. Cette application mobile communique directement avec le backend Odoo pour l’accès aux données.

🧩 Architecture et technologies utilisées

Backend : Odoo + ORM Odoo + PostgreSQL

Frontend mobile : Android (Java)

Communication réseau :

Retrofit (API REST)

OkHttp (client HTTP)

Géolocalisation : Google Location Services

Toutes les données sont stockées et gérées via l’ORM Odoo, garantissant sécurité, cohérence et maintenabilité.

🔍 Fonctionnalités de l’application

L’application permet à l’utilisateur de :

🔎 Rechercher des pharmacies de proximité ou de permanence

🏙️ Effectuer une recherche :

par ville

par nom de pharmacie

📍 Visualiser :

l’itinéraire vers une pharmacie

les médicaments disponibles

🌐 Utiliser la géolocalisation pour afficher les 10 pharmacies les plus proches

⚙️ Prérequis pour l’exécution

Pour que l’application fonctionne correctement, il est nécessaire de :

✔️ Installer et lancer le module Odoo Pharmacie

✔️ Démarrer le serveur Odoo en local

✔️ Connecter le PC (serveur Odoo) et le téléphone Android au même réseau Wi-Fi

✔️ Autoriser le trafic HTTP (cleartext)

🛠️ Configuration requise 1️⃣ Modifier l’adresse IP dans MainActivity.java

Remplacer l’adresse IP par celle de votre PC :

Retrofit retrofit = new Retrofit.Builder() .baseUrl("http://192.168.1.40:8069/") .addConverterFactory(GsonConverterFactory.create()) .client(okHttpClient) .build();

👉 L’IP doit correspondre à celle obtenue via la commande :

ipconfig

2️⃣ Modifier network_security_config.xml

Dans le fichier /res/xml/network_security_config.xml, remplacer également l’adresse IP :

192.168.1.40
⚠️ Cette configuration est nécessaire pour autoriser les communications HTTP entre l’application Android et le serveur Odoo.

✅ Résumé

Cette application mobile constitue une extension naturelle du module Odoo, permettant un accès mobile fluide et géolocalisé aux pharmacies, tout en s’appuyant sur un backend ERP robuste et sécurisé.
