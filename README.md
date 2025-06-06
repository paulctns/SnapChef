# SnapChef

## Descriere

**SnapChef** este o aplicație Android care îți permite să faci o fotografie cu camera dispozitivului, să detectezi automat alimentele din imagine folosind API-ul Gemini și să generezi rețete culinare simple pe baza ingredientelor detectate.

---

## Funcționalități

- Captură foto direct din aplicație.
- Detectare automată a alimentelor din imagine.
- Generare automată de rețete culinare cu pași de preparare și valori nutriționale.
- Istoric al rețetelor generate accesibil din meniul lateral (Navigation Drawer).

---

## Cerințe

- Android 6.0 (API 23) sau mai nou.
- Permisiuni pentru cameră și stocare.
- Conexiune la internet pentru API-ul Gemini.

---

## Instalare

1. Deschide proiectul în Android Studio.

2. Configurează API-ul Gemini (cheie sau setări necesare).

3. Rulează aplicația pe un dispozitiv Android real sau emulator.

---

## Utilizare

1. Apasă butonul de cameră pentru a face o fotografie.

2. Imaginea este trimisă la API-ul Gemini pentru identificarea alimentelor.

3. Aplicația primește lista ingredientelor și afișează rezultatul.

4. Pe baza ingredientelor, aplicația generează 3 rețete simple.

5. Rețetele sunt afișate și salvate în istoricul accesibil din meniul lateral.

---

## Arhitectură

- Activitate principală `MainActivity` gestionează UI și interacțiunea cu utilizatorul.

- Clasa `RecipeEntry` modelează o intrare de rețetă cu ingrediente și instrucțiuni.

- `MessageAdapter` gestionează afișarea mesajelor în lista derulantă.

- Comunicarea cu API-ul Gemini se face asincron prin `GeminiHelper`.

- Istoricul rețetelor este păstrat temporar într-o listă statică.

