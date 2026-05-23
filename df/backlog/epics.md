# Initial Epics

## EPIC-01 — Platform Foundation

Create the API-first core platform foundation using the preferred Java Spring and PostgreSQL direction.

## EPIC-02 — Sovereign Country Deployment Model

Support country/ministry-operated infrastructure, country-level data sovereignty, country-owned deployment pipelines, and vendor-provided release packages.

## EPIC-03 — Configuration and Inheritance Engine

Enable configuration inheritance from country to region, city, institution, academic unit, and user-facing levels. Lower levels can extend allowed schema areas but cannot break locked upper-level rules unless explicitly creating an inheritance break.

## EPIC-04 — Framework Release and Update Manager

Support centralized framework releases through versioned packages, compatibility checks, migration plans, release notes, rollback guidance, and country approval workflows.

## EPIC-05 — Country Template System

Create evidence-based, versioned country education templates based on public/open-source research.

## EPIC-06 — Poland MVP Template

Research and define the Poland country configuration as the first MVP reference template.

## EPIC-07 — Institution Schema Packs

Create schema-pack architecture for school, kindergarten, university, and future institution types.

## EPIC-08 — User, Role, and Access Management

Support global/country/state/city/institution/class roles, including admins, teachers, students, parents, guardians, lecturers, and university roles.

## EPIC-09 — School Core Module

Support classes, subjects, teachers, students, parents, schedules, attendance, gradebook, homework, exams, and school-level dashboards.

## EPIC-10 — Kindergarten Core Module

Support child groups, caregivers, daily routines, check-in/check-out, meals, allergies, authorized pickup persons, parent communication, and developmental notes.

## EPIC-11 — University Core Module

Support faculties, departments, degree programs, courses, lecturers, assistants, ECTS/credits, enrollment, exams, retakes, transcripts, and thesis workflows.

## EPIC-12 — Meal and Catering Management

Support school/kindergarten-level catering configuration, menus, meal subscriptions, parent-visible paid/unpaid status, meal exclusions, and end-of-month credit/balance adjustment.

## EPIC-13 — Attendance

Support attendance across schools, kindergartens, and universities with institution-specific rules.

## EPIC-14 — Gradebook and Assessment

Support configurable grading scales, semester/final grade calculation, manual correction, audit trail, and institution-specific assessment models.

## EPIC-15 — Homework and Assignments

Support homework assignment, submission, review, teacher comments, and AI-assisted learning boundaries.

## EPIC-16 — AI Student Assistant

Provide AI help that explains topics, asks guiding questions, and shows similar solved examples without giving direct answers to assigned tasks.

## EPIC-17 — AI Teacher Assistant

Help teachers prepare lessons, exercises, tests, rubrics, explanations, substitute lessons, and personalized learning materials.

## EPIC-18 — Dashboards and Statistics

Provide real-time dashboards and statistics at country, region, city, school, class, teacher, parent, and student levels.

## EPIC-19 — Security, Privacy, and Audit

Design for maximum security, zero-trust access, strong isolation, encryption, monitoring, auditability, compliance, and data sovereignty.

## EPIC-20 — Backlog and Issue Tracker Integration

Maintain Markdown backlog first, then support sync/import into Jira or another tracker.

## EPIC-21 — Message sharing system, Forums, Chats

Allow teachers to share messages in group chats, post questions and answers on forums, and send direct messages to other users. Support message threading, attachments, and notifications.

## EPIC-22 — Internationalisation (i18n)

Support as many human languages as possible from the very first release. Every visible label, message, and piece of UI text must be translatable and persisted in the database — no hard-coded strings in code or template files. Language availability is governed by a global default list that any country deployment may restrict to a subset via its country configuration. No language-specific conditional code is permitted anywhere in the framework; all language differences are handled exclusively through data.

### Default language list

The following languages are included in the global default catalogue. A country config may activate a subset; all others remain dormant but never removed from the catalogue.

English, Mandarin Chinese, Hindi, Spanish, French, Modern Standard Arabic, Bengali, Russian, Portuguese, Urdu, Indonesian, German, Japanese, Nigerian Pidgin, Marathi, Telugu, Turkish, Tamil, Yue Chinese, Vietnamese, Tagalog, Wu Chinese, Korean, Persian, Hausa, Egyptian Arabic, Swahili, Javanese, Italian, Western Punjabi, Gujarati, Thai, Kannada, Amharic, Bhojpuri, Eastern Punjabi, Malayalam, Burmese, Odia, Maithili, Ukrainian, Sindhi, Algerian Arabic, Moroccan Arabic, Sudanese Arabic, Nepali, Sinhala, Uzbek, Dutch, Kurdish, Igbo, Somali, North Levantine Arabic, Romanian, Zhuang, Azerbaijani, Greek, Chittagonian, Kazakh, Deccan, Hungarian, Kinyarwanda, South Levantine Arabic, Czech, Min Nan Chinese, Sylheti, Zulu, Belarusian, Quechua, Madurese, Cebuano, Mesopotamian Arabic, Assamese, Swedish, Hakka Chinese, Tajik, Ilocano, Hebrew, Shona, Uyghur, Hmong, Catalan, Fula, Balinese, Armenian, Serbo-Croatian, Danish, Finnish, Slovak, Lao, Lithuanian, Norwegian, Croatian, Slovenian, Latvian, Estonian, Polish, Bulgarian, Serbian, Bosnian, Montenegrin, Macedonian, Albanian, Icelandic, Faroese, Irish, Scottish Gaelic, Welsh, Manx, Cornish, Breton, Frisian, West Frisian, North Frisian, Saterland Frisian, Low German, Luxembourgish, Yiddish, Romani, Sinti Romani, Vlax Romani, Balkan Romani, Basque, Galician, Asturian, Leonese, Aragonese, Mirandese, Occitan, Provençal, Gascon, Corsican, Sardinian, Sicilian, Neapolitan, Venetian, Lombard, Piedmontese, Ligurian, Emilian, Romagnol, Friulian, Ladin, Romansh, Aromanian, Megleno-Romanian, Istro-Romanian, Maltese, Latin, Bavarian, Alemannic German, Swiss German, Kashubian, Silesian, Sorbian, Upper Sorbian, Lower Sorbian, Rusyn, Old Church Slavonic, Võro, Seto, Karelian, Veps, Livonian, Ingrian, Ludic, Northern Sami, Lule Sami, Southern Sami, Inari Sami, Skolt Sami, Kildin Sami, Erzya, Moksha, Mari, Hill Mari, Komi, Komi-Permyak, Udmurt, Nenets, Tatar, Bashkir, Chuvash, Gagauz, Crimean Tatar, Karaim, Karachay-Balkar, Kumyk, Nogai, Kalmyk, Georgian, Mingrelian, Laz, Svan, Abkhaz, Abaza, Adyghe, Kabardian, Chechen, Ingush, Avar, Dargwa, Lak, Lezgian, Tabasaran, Rutul, Tsakhur, Aghul, Udi, Ossetian, Talysh, Arabic Cypriot, Greek Cypriot, Turkish Cypriot.

