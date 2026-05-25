# QA Report — SPIKE-001

- Task: `SPIKE-001`
- QA role session: 2026-05-23 local
- Artifact reviewed: `df/artifacts/SPIKE-001/poland-template-v1.md`
- State at entry: `READY_FOR_QA`
- State at exit: `READY_FOR_PO`

---

## QA Result: PASS

- Task: SPIKE-001
- Acceptance criteria covered: YES — all 7 criteria verified below
- Unit tests: N/A (research spike, no code produced)
- Integration tests: N/A (research spike, no code produced)
- Manual checks: Full document review against all 7 acceptance criteria; source traceability spot-check; assumption/fact separation assessment; framework guardrail compliance check
- Regression checks: N/A (no code changes in this spike)
- Risks: See Section 4 below
- Handoff: READY_FOR_PO

---

## 1. Test case results

### TC-001 — AC1: Sources are listed

**Check:** All data claims must be backed by a cited source. Sources must be public/official references.

| Finding | Result |
|---|---|
| Source register present in Section 2 | ✅ |
| 14 sources identified (SRC-01 to SRC-14) | ✅ |
| Sources include EU reference (Eurydice/Eurypedia), official CKE exam portal, and University of Warsaw academic calendar | ✅ |
| All URLs present and source types described | ✅ |
| In-text citations link claims to source IDs (e.g., `SRC-04`, `SRC-10`, `SRC-11`) | ✅ |

**Result: PASS**

---

### TC-002 — AC2: Education levels are documented

**Check:** All stages from pre-school to higher education must be represented.

| Finding | Result |
|---|---|
| Pre-school (ISCED 010, 020) levels documented | ✅ |
| Mandatory preschool year (age 6) documented | ✅ |
| Primary school 8-year single structure (ISCED 1+2, SRC-01, SRC-03) documented | ✅ |
| General secondary 4-year (`liceum`, ISCED 3) documented | ✅ |
| Technical secondary 5-year (`technikum`, ISCED 3) documented | ✅ |
| Sectoral vocational Stage I and Stage II documented | ✅ |
| Post-secondary non-tertiary documented | ✅ |
| Higher education first cycle, second cycle, long cycle, doctoral documented | ✅ |
| ISCED codes and typical age ranges present for each level | ✅ |
| 2017 post-reform structure used as baseline (stated explicitly) | ✅ |

**Result: PASS**

---

### TC-003 — AC3: Institution types are documented

**Check:** All relevant school types, kindergartens, universities, and vocational institutions must be listed.

| Finding | Result |
|---|---|
| 17 institution type keys documented in Section 4 | ✅ |
| Polish label (`żłobek`, `przedszkole`, `szkoła podstawowa`, etc.) present for each type | ✅ |
| Childcare-range types (0-3) listed with correct scope note | ✅ |
| Preschool subtypes listed | ✅ |
| Primary school type present | ✅ |
| General secondary, technical secondary, sectoral vocational types present | ✅ |
| Special job-preparation school noted with scope caveat | ✅ |
| Post-secondary school present with duration note | ✅ |
| University-type and non-university HEI types present | ✅ |
| Doctoral provider type present | ✅ |

**Result: PASS**

---

### TC-004 — AC4: Grade scale is documented

**Check:** All grade scales used across education levels must be specified.

| Finding | Result |
|---|---|
| Preschool: descriptive/observational mode documented (Section 5.1) | ✅ |
| Primary grades 1-3: descriptive end-of-year assessment documented (SRC-04) | ✅ |
| Primary grades 4-8: numeric scale 6 (excellent) to 1 (unsatisfactory) with labels (SRC-04) | ✅ |
| Behaviour scale documented separately (grades 4+, descriptive/category) | ✅ |
| General secondary / post-secondary: same 6..1 scale confirmed (SRC-05, SRC-07) | ✅ |
| Matura pass threshold documented: ≥30% per compulsory subject (SRC-05) | ✅ |
| Vocational qualification exam thresholds documented: ≥50% written, ≥75% practical (SRC-06) | ✅ |
| Grade 8 external exam noted as separate criterion profile | ✅ |
| Higher education: explicitly not a single national scale — marked `institution_configurable` with rationale (SRC-08, SRC-09) | ✅ |
| Template recommendation config block present in Section 8 | ✅ |

**Result: PASS**

---

### TC-005 — AC5: Semester/school-year assumptions are documented

**Check:** Start/end dates, term structure, and holidays must be documented.

| Finding | Result |
|---|---|
| School year start (September), end (June) documented | ✅ |
| 2-semester term model documented | ✅ |
| Winter break (voivodeship-specific) noted | ✅ |
| Christmas, Easter/spring breaks noted | ✅ |
| Summer holiday period noted | ✅ |
| Caution note about annual ministry validation present | ✅ |
| Higher education academic year start (October) documented with source (SRC-09) | ✅ |
| HE semester pattern (Oct-Feb, Feb/Mar-Jun) documented with source | ✅ |
| University of Warsaw 2025/2026 concrete dates provided as example (SRC-12) | ✅ |
| HE exam session pattern documented | ✅ |

**Result: PASS**

---

### TC-006 — AC6: Common subjects are proposed

**Check:** Core curriculum subjects must be proposed for each education level.

| Finding | Result |
|---|---|
| Preschool learning areas proposed (8 areas — clearly marked as validation-pending) | ✅ |
| Primary grades 1-3 subjects derived from SRC-10 (10 subjects listed) | ✅ |
| Primary grades 4-8 subjects derived from SRC-10 (18 subjects listed) | ✅ |
| General secondary (`liceum`) subjects derived from SRC-11 (14 subjects listed) | ✅ |
| Technical/sectoral vocational core subjects listed; occupation-specific modules correctly noted as requiring separate research | ✅ |
| Higher education: correctly NOT hard-coded; institution/programme-level configuration recommended | ✅ |
| Religion/ethics, optional subjects noted as edge cases | ✅ |

**Result: PASS**

---

### TC-007 — AC7: Unknowns are listed for validation

**Check:** All items that cannot be confirmed from public sources alone must be explicitly listed.

| Finding | Result |
|---|---|
| 8 unknowns listed in Section 9 | ✅ |
| Higher-education grading national standard: open ✅ |
| Exact yearly school dates: open ✅ |
| Voivodeship winter-break mapping: open ✅ |
| Preschool subject taxonomy confirmation: open ✅ |
| Special-education pathways: open ✅ |
| Vocational qualification catalog: open ✅ |
| Minority-language, religion/ethics, optional-subject rules: open ✅ |
| Childcare (0-3) MVP scope: open ✅ |
| All unknowns are actionable (each states what validation is needed) | ✅ |

**Result: PASS**

---

## 2. Framework guardrail compliance check

**Check:** The Poland Template must be data-only and must not influence framework code, schema, structure, or API.

| Finding | Result |
|---|---|
| Section "Strict implementation guardrail" present at top of document | ✅ |
| Explicit prohibition on country-specific code, framework branch, schema fork, structural/module change | ✅ |
| Section 8 proposal block explicitly labelled "data/configuration only" | ✅ |
| Section 7.6 (HE) explicitly states do not hard-code national subject defaults | ✅ |
| No code artifacts produced by this spike | ✅ |

**Result: PASS**

---

## 3. Assumption vs confirmed fact separation

**Check:** Confirmed information (sourced) must be clearly distinguished from working assumptions and proposals.

| Finding | Result |
|---|---|
| Source citations (SRC-XX) used consistently for confirmed facts | ✅ |
| "Template recommendation" label used for derived proposals | ✅ |
| "Working assumption" label used in Section 5.4 for HE grading | ✅ |
| "Proposed for validation" label used in Section 7.1 for preschool subjects | ✅ |
| "Important caution" notes present where source access was incomplete (Section 6.1) | ✅ |
| Section 9 explicitly separates unknowns requiring external validation | ✅ |

**Result: PASS**

---

## 4. Risks for PO

| # | Risk | Severity | Recommendation |
|---|---|---|---|
| R-01 | Higher-education grading scale is not confirmed nationally — coding a single HE scale as a Poland default before validation would be incorrect | Medium | PO to accept HE grading as institution-configurable in MVP |
| R-02 | School-year exact dates change annually via ministry notice — any implementation must reference annual versioned data, not hard-coded values | Medium | PO to confirm calendar versioning approach in schema design task |
| R-03 | Voivodeship-level winter break variation will require region-aware date configuration if accurate scheduling is needed | Low | Defer to a follow-up task once regional configuration is scoped |
| R-04 | Preschool subject taxonomy requires product decision (learning areas vs subject list) before schema design | Low | PO to decide approach during next sprint planning |
| R-05 | Vocational qualification catalog (occupation codes, module names) was out of scope for this spike — secondary vocational configuration will need a separate dedicated spike | Medium | Add to backlog as a follow-up spike |

---

## 5. Defects

None. All acceptance criteria passed. No defects raised.

---

## 6. QA sign-off

- QA role: `qa`
- Date: 2026-05-23 local
- Decision: **PASS — READY_FOR_PO**
- Handoff: PO should validate the research content aligns with business intent and confirm/accept the listed unknowns as appropriate deferral items.

