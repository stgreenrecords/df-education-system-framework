# Poland Template v1

- Task: `SPIKE-001`
- Version: `v2026.1-draft`
- Status: PO-accepted draft research baseline
- Prepared by: `dev`
- Timestamp: 2026-05-23 08:50 local
- PO acceptance: 2026-05-23 09:55 local
- Scope: Poland national baseline for MVP country-template research
- Intended use: Source-backed seed data for future country-configuration implementation

## 1. Executive summary

This document captures a first-pass Poland country template for the Education System Framework using public/open sources that were accessible from the current environment. It covers:

- education levels from early childhood to higher education;
- institution types;
- school and post-secondary grading models;
- higher-education structure and calendar assumptions;
- school-year and semester assumptions for application design;
- proposed common subjects for core education levels;
- explicit unknowns that require QA/PO or domain validation before coding.

Baseline assumption: the template should model the post-2017 Polish structure as the current national default, while allowing yearly and institution-level overrides.

## Strict implementation guardrail

Poland is a **reference country dataset only**. This template must have **no influence** on framework code, framework structure, database schemas, or API contracts.

Strict rule:

- no country-specific code change;
- no Poland-specific framework branch;
- no Poland-specific schema fork;
- no Poland-specific structural/module change.

Only configuration data, versioned template content, and actual country-specific values may differ by country.

## 2. Source register

| ID | Source | Type | What it supports | Accessed |
|---|---|---|---|---|
| SRC-01 | Eurydice Poland Overview — <https://eurydice.eacea.ec.europa.eu/national-education-systems/poland/overview> | Public EU education reference | overall system structure, compulsory education, education stages | 2026-05-23 |
| SRC-02 | Eurydice Poland Early childhood education and care — <https://eurydice.eacea.ec.europa.eu/national-education-systems/poland/early-childhood-education-and-care> | Public EU education reference | ages 0-3 and 3-6/7, preschool institution types | 2026-05-23 |
| SRC-03 | Eurypedia Poland Organisation of the education system and its structure — <https://eurydice.eacea.ec.europa.eu/eurypedia/poland/organisation-education-system-and-its-structure> | Public EU education reference | 8-year primary, 4-year liceum, 5-year technikum, stage I/II sectoral vocational, post-secondary school, age 18 rule | 2026-05-23 |
| SRC-04 | Eurypedia Poland Assessment in single-structure education — <https://eurydice.eacea.ec.europa.eu/eurypedia/poland/assessment-single-structure-education> | Public EU education reference | primary grading scale, descriptive assessment in early grades, behaviour marks | 2026-05-23 |
| SRC-05 | Eurypedia Poland Assessment in general upper secondary education — <https://eurydice.eacea.ec.europa.eu/eurypedia/poland/assessment-general-upper-secondary-education> | Public EU education reference | matura structure and pass threshold | 2026-05-23 |
| SRC-06 | Eurypedia Poland Assessment in vocational upper secondary education — <https://eurydice.eacea.ec.europa.eu/eurypedia/poland/assessment-vocational-upper-secondary-education> | Public EU education reference | vocational qualification exam thresholds | 2026-05-23 |
| SRC-07 | Eurypedia Poland Assessment in post-secondary non-tertiary education — <https://eurydice.eacea.ec.europa.eu/eurypedia/poland/assessment-post-secondary-non-tertiary-education> | Public EU education reference | post-secondary grading scale | 2026-05-23 |
| SRC-08 | Eurydice Poland Higher education — <https://eurydice.eacea.ec.europa.eu/national-education-systems/poland/higher-education> | Public EU education reference | first-cycle, second-cycle, long-cycle, doctoral, postgraduate structure | 2026-05-23 |
| SRC-09 | Eurypedia Poland Organisation of the academic year — <https://eurydice.eacea.ec.europa.eu/eurypedia/poland/organisation-academic-year> | Public EU education reference | higher-education academic-year structure, semester break pattern | 2026-05-23 |
| SRC-10 | Eurypedia Poland Teaching and learning in single-structure education — <https://eurydice.eacea.ec.europa.eu/eurypedia/poland/teaching-and-learning-single-structure-education> | Public EU education reference | primary subject set and curriculum-hour evidence | 2026-05-23 |
| SRC-11 | Eurypedia Poland Teaching and learning in general upper secondary education — <https://eurydice.eacea.ec.europa.eu/eurypedia/poland/teaching-and-learning-general-upper-secondary-education> | Public EU education reference | general secondary subject set and curriculum-hour evidence | 2026-05-23 |
| SRC-12 | University of Warsaw academic calendar — <https://welcome.uw.edu.pl/academics/academic-calendar/> | Public university source | example 2025/2026 higher-education semester dates and breaks | 2026-05-23 |
| SRC-13 | Central Examination Board (CKE) matura portal — <https://cke.gov.pl/egzamin-maturalny/> | Official Polish examination portal | confirms official exam category exists and should be modeled in the template | 2026-05-23 |
| SRC-14 | Central Examination Board (CKE) grade-8 exam portal — <https://cke.gov.pl/egzamin-osmoklasisty/> | Official Polish examination portal | confirms official grade-8 exam category exists and should be modeled in the template | 2026-05-23 |

## 3. Education levels

| Level | Typical age | ISCED / stage | Main institutions | Poland template recommendation |
|---|---|---|---|---|
| Early childhood care | 0-3 | ISCED 010 / pre-education care | crèche (`żłobek`), kids’ club, day-care provider, nanny | Keep as optional scope for MVP unless the product explicitly supports childcare operations |
| Preschool education | 3-6/7 | ISCED 020 | nursery school (`przedszkole`), preschool class in primary school, preschool unit, preschool centre | Represent as `preschool` with institution subtypes |
| Mandatory preschool year | usually age 6 | pre-primary bridge year | same preschool institutions as above | Model as required pre-primary year before entry to grade 1 |
| Primary school | 7-15 | single-structure ISCED 1+2 | 8-year primary school (`szkoła podstawowa`) | Core national school template level |
| General secondary | usually 15-19 | ISCED 3 | 4-year general secondary school (`liceum ogólnokształcące`) | Core upper-secondary pathway |
| Technical secondary | usually 15-20 | ISCED 3 | 5-year technical secondary school (`technikum`) | Upper-secondary pathway with both matura and vocational outcomes |
| Sectoral vocational stage I | usually 15-18 | ISCED 3 | 3-year stage I sectoral vocational school (`branżowa szkoła I stopnia`) | Vocational pathway leading to qualification exam |
| Sectoral vocational stage II | usually 18-20 | ISCED 3 continuation | 2-year stage II sectoral vocational school (`branżowa szkoła II stopnia`) | Advanced continuation pathway; can lead to matura and vocational exam |
| Special job-preparation school | varies | special pathway | school preparing for employment | Out of initial MVP unless special-education flows are in scope |
| Post-secondary non-tertiary | after secondary | non-tertiary | post-secondary school (`szkoła policealna`) | Separate vocational/professional path with semester-based progression |
| Higher education – first cycle | 18+ | ISCED 6 | university-type and non-university HEIs | Model as programme-level tertiary pathway |
| Higher education – second cycle | after first cycle | ISCED 7 | HEIs | Model as continuation pathway to Master’s degree |
| Higher education – long cycle | 18+ | ISCED 7 | selected HEIs/programmes | Needed for fields such as medicine/law-type long-cycle programmes |
| Doctoral education | post-Master | ISCED 8 | HEIs, research institutions, international research institutes | Usually outside MVP school scope; keep extensible |

### Source-backed notes

- Full-time compulsory education lasts 12 years and includes the last preschool year plus 8 years of primary education, continuing until age 18 in school or training settings (`SRC-01`, `SRC-03`).
- All 6-year-olds must attend one year of preschool before grade 1, and primary school starts at age 7 (`SRC-01`, `SRC-03`).
- Primary school is a single 8-year structure split into grades 1-3 (integrated early education) and grades 4-8 (subject-based education) (`SRC-01`, `SRC-03`).

## 4. Institution types

| Template key | Polish label / common label | Applies to | Notes |
|---|---|---|---|
| `creche` | `żłobek` | 0-3 childcare | Often outside school-system MVP scope |
| `kids_club` | `klub dziecięcy` | 0-3 childcare | Optional if childcare is modeled |
| `day_care_provider` | `opiekun dzienny` | 0-3 childcare | Optional if childcare is modeled |
| `nanny_based_care` | `niania` | 0-3 childcare | Usually not an institution record |
| `preschool` | `przedszkole` | ages 3-6/7 | Main preschool institution |
| `preschool_class_in_primary` | `oddział przedszkolny w szkole podstawowej` | ages 3-6/7 | Preschool delivered inside primary school |
| `preschool_unit` | `zespół wychowania przedszkolnego` | preschool | Smaller-format provision |
| `preschool_centre` | `punkt przedszkolny` | preschool | Smaller-format provision |
| `primary_school` | `szkoła podstawowa` | primary | National default school type |
| `general_secondary_school` | `liceum ogólnokształcące` | upper secondary | Academic track |
| `technical_secondary_school` | `technikum` | upper secondary | Academic + vocational track |
| `sectoral_vocational_school_stage_1` | `branżowa szkoła I stopnia` | upper secondary | Qualification-focused vocational path |
| `sectoral_vocational_school_stage_2` | `branżowa szkoła II stopnia` | upper secondary continuation | Enables continued vocational + matura path |
| `special_job_preparation_school` | `szkoła specjalna przysposabiająca do pracy` | special pathway | Requires separate special-education analysis |
| `post_secondary_school` | `szkoła policealna` | post-secondary non-tertiary | Up to 2.5 years |
| `university_type_hei` | `uczelnia akademicka` | higher education | University-type HEI |
| `non_university_hei` | `uczelnia zawodowa` | higher education | Practice-oriented HEI |
| `research_institution_doctoral_provider` | research institution / institute | doctoral | Needed only if doctoral scope is included |

## 5. Grade scale and assessment model

## 5.1 Early childhood and preschool

- For preschool/pre-primary, the national pattern is better modeled as developmental/descriptive observation rather than a universal numeric grade scale.
- Recommendation for the template: support `assessmentMode = descriptive` for preschool levels.

## 5.2 Primary school (single-structure)

`SRC-04` states:

- grades 1-3 use descriptive end-of-year assessment for progression;
- grades 4-8 use the numeric scale:
  - `6 = excellent`
  - `5 = very good`
  - `4 = good`
  - `3 = satisfactory`
  - `2 = acceptable`
  - `1 = unsatisfactory`
- behaviour is assessed separately; from grade 4 the behaviour scale is descriptive/category-based (`excellent`, `very good`, `good`, `acceptable`, `unacceptable`, `inadmissible`).

### Template recommendation

```text
school_grade_scale_default = [6, 5, 4, 3, 2, 1]
school_grade_scale_labels = excellent, very_good, good, satisfactory, acceptable, unsatisfactory
early_primary_assessment_mode = descriptive
behaviour_scale = separate
```

## 5.3 General secondary, technical secondary, sectoral vocational, post-secondary

The accessible public evidence supports continuing use of the school-style 1-6 grading logic across upper-secondary/post-secondary schooling, with external exams layered on top:

- `SRC-05`: matura pass requires at least `30%` in each compulsory subject (written and oral where applicable) plus participation in one additional advanced-level subject.
- `SRC-06`: vocational qualification exams require at least `50%` in the written part and `75%` in the practical part.
- `SRC-07`: post-secondary non-tertiary schools use the same grading scale wording: `6 excellent`, `5 very good`, `4 good`, `3 satisfactory`, `2 acceptable`, `1 unsatisfactory`.

### Template recommendation

- Use the default school grade scale `6..1` for primary, general secondary, technical secondary, sectoral vocational, and post-secondary institutions.
- Store external exam results separately from coursework marks:
  - `grade_8_exam`: criterion/result profile
  - `matura`: percentage by subject + pass flag
  - `vocational_exam`: written percentage, practical percentage, qualification certificate outcome

## 5.4 Higher education

Accessible sources in this spike clearly confirm tertiary programme structure (`SRC-08`, `SRC-09`, `SRC-12`) but do **not** provide a single national grading scale that can be safely treated as uniform across all HEIs.

### Template recommendation

- Model higher-education grading as `institutionConfigurable`.
- Working assumption for future implementation: many Polish HEIs use a 2.0-5.0 style scale, but this was **not** validated as a national standard in this spike.
- QA/PO should treat higher-education grade-scale settings as an open validation item before coding a Poland-wide default.

## 6. Semester and school-year assumptions

## 6.1 School education (national template assumptions)

The school-level yearly calendar should be treated as **versioned annual data**, not a permanent hard-coded constant.

### Recommended MVP defaults

- `schoolYear.startMonth = September`
- `schoolYear.endMonth = June`
- `schoolYear.termModel = 2 semesters`
- `winterBreak = voivodeship-specific`
- `christmasBreak = yes`
- `spring/EasterBreak = yes`
- `summerHoliday = late June through August`

### Important caution

Direct CLI extraction of the current ministry school-year page was incomplete in this environment, so exact date ranges for the school system should be validated against the annual ministry notice before implementation.

## 6.2 Higher education (source-backed)

`SRC-09` and `SRC-12` support the following higher-education pattern:

- academic year begins in October;
- teaching activities generally run October-February and February/March-June;
- there is a Christmas/Epiphany break of about two weeks;
- there is a break between semesters, usually 1-2 weeks in February;
- examination sessions occur after each teaching period;
- University of Warsaw 2025/2026 shows a concrete example with winter semester `01.10.2025-15.02.2026` and winter holidays `22.12.2025-06.01.2026` (`SRC-12`).

### Template recommendation

```text
higherEd.yearStart = 1 October
higherEd.termModel = 2 semesters
higherEd.semesterBreak = February (1-2 weeks typical)
higherEd.christmasBreak = late December to early January
higherEd.examSessions = end of each semester
```

## 7. Proposed common subjects

These are **template-ready subject families** derived from the public curriculum tables. They should be implemented as defaults, with programme/school overrides allowed.

## 7.1 Preschool / pre-primary (proposed learning areas)

Because this spike did not extract a dedicated preschool curriculum table, model preschool first as learning areas rather than rigid subject timetables:

- language and communication (Polish)
- social-emotional development
- early mathematics readiness
- environmental / nature awareness
- artistic activity
- music / rhythmics
- physical development
- foreign-language exposure/readiness

Status: proposed for validation.

## 7.2 Primary grades 1-3

Derived from `SRC-10` integrated teaching:

- Polish language
- social education
- natural sciences
- mathematics
- technology
- modern foreign language
- music education
- art education
- ICT / computer education
- physical education

## 7.3 Primary grades 4-8

Derived from `SRC-10` subject tables:

- Polish language
- modern foreign language
- second modern foreign language (from later grades / school offering)
- music
- art education
- history
- civic education
- natural sciences
- geography
- biology
- chemistry
- physics
- mathematics
- computer science / ICT
- technology
- physical education
- safety education
- religion or ethics (separate-regulation subject)

## 7.4 General secondary (`liceum`)

Derived from `SRC-11`:

- Polish language
- modern foreign language
- second modern foreign language or Latin
- philosophy / visual arts / music / Latin and ancient culture (school selection)
- history
- citizenship education
- business and management
- geography
- biology
- chemistry
- physics
- mathematics
- computer science / IT
- physical education
- safety education

## 7.5 Technical and sectoral vocational pathways

Use the general-education core above **plus** vocation-specific modules:

- Polish language
- mathematics
- foreign language
- history / civics as applicable
- science subjects as pathway requires
- computer science / IT
- physical education
- safety education
- vocational theory modules
- vocational practical training / workshop / placement modules

Exact subject/module names depend on the occupation and qualification catalog and should be researched separately before code implementation.

## 7.6 Higher education

Do not hard-code national subject defaults. Higher-education subjects/modules should be configured at institution and programme level.

## 8. Poland Template v1 — implementation-oriented proposal

This proposal is **data/configuration only**. It defines candidate values for a generic country-template model and must not be used to justify country-specific framework implementation.

```text
countryCode = PL
primaryLanguage = pl-PL
status = draft
calendarModel = yearly_versioned
schoolEducation.compulsoryPreschoolYear = true
schoolEducation.primary.durationYears = 8
schoolEducation.secondary.general.durationYears = 4
schoolEducation.secondary.technical.durationYears = 5
schoolEducation.secondary.sectoralStage1.durationYears = 3
schoolEducation.secondary.sectoralStage2.durationYears = 2
schoolEducation.postSecondary.maxDurationYears = 2.5
schoolEducation.defaultGradeScale = 6..1
schoolEducation.defaultAssessmentEarlyPrimary = descriptive
schoolEducation.externalExams = [grade8_exam, matura, vocational_exam]
higherEducation.programmeTypes = [first_cycle, second_cycle, long_cycle, doctoral, postgraduate]
higherEducation.gradeScale = institution_configurable
```

## 9. Unknowns requiring validation

1. **Higher-education grading** — this spike did not validate a single national HE grade scale. Treat HE grading as institution-specific until confirmed.
2. **Exact yearly school dates** — school start/end and holiday dates change by academic year; do not hard-code them without annual ministry validation.
3. **Voivodeship winter-break mapping** — winter holidays vary by region and year; if the product needs date-accurate scheduling, this must be stored as yearly regional data.
4. **Preschool subject taxonomy** — preschool should likely use developmental learning areas rather than classic school subjects; confirm product needs before schema design.
5. **Special-education pathways** — this template only notes them; it does not fully model special schools and special job-preparation flows.
6. **Vocational qualification catalog** — exact occupation codes, module names, and qualification mappings were out of scope for this spike.
7. **Minority-language / Kashubian / religion-ethics / optional-subject rules** — public curriculum references show they exist, but detailed national default rules were not fully extracted in this spike.
8. **0-3 childcare scope** — confirm whether the MVP country template should include care settings under the same country configuration as schools.

## 10. Acceptance-criteria coverage checklist

- [x] Sources are listed
- [x] Education levels are documented
- [x] Institution types are documented
- [x] Grade scale is documented
- [x] Semester/school-year assumptions are documented
- [x] Common subjects are proposed
- [x] Unknowns are listed for validation

