import Link from "next/link";

const valueBlocks = ["Teaching and learning", "Institution coordination", "Secure operations"];
const audienceBlocks = ["Student", "Teacher", "Parent", "Institution"];

export default function HomePage() {
  return (
    <main className="page-shell">
      <section className="surface-card page-panel" aria-labelledby="home-page-title">
        <header className="top-nav">
          <div>
            <p className="eyebrow">Generic education platform</p>
            <h1 id="home-page-title">Manage learning across institutions with one secure platform</h1>
          </div>
          <nav aria-label="Primary navigation" className="nav-actions">
            <Link className="text-link" href="/#institution-selection">
              Institution selection
            </Link>
            <Link className="text-link" href="/login">
              Account login
            </Link>
          </nav>
        </header>

        <section className="hero-block" aria-labelledby="hero-title">
          <p className="eyebrow">Banner / hero</p>
          <h2 id="hero-title">Manage learning across institutions with one secure platform</h2>
          <p>
            Choose your institution or sign in to continue with your learning workspace.
          </p>
          <div className="actions-row">
            <Link className="button-link button-link--primary" href="/#institution-selection">
              Institution selection
            </Link>
            <Link className="button-link" href="/login">
              Account login
            </Link>
          </div>
        </section>

        <section className="content-grid content-grid--two" id="institution-selection">
          <article className="content-card">
            <h2>Institution selection preview</h2>
            <p>Search / select institution block</p>
            <p className="muted-copy">Recent institutions and quick categories are shown here.</p>
          </article>
          <article className="content-card">
            <h2>Institution states</h2>
            <ul className="detail-list">
              <li>Loading: institution preview cards render as placeholders.</li>
              <li>Empty: No recent institutions yet.</li>
              <li>Error: Institution lookup retry appears inline.</li>
            </ul>
          </article>
        </section>

        <section aria-labelledby="value-blocks-title" className="content-stack">
          <h2 id="value-blocks-title">Platform value blocks</h2>
          <div className="content-grid content-grid--three">
            {valueBlocks.map((block) => (
              <article className="content-card" key={block}>
                <h3>{block}</h3>
                <p className="muted-copy">Low-fidelity feature summary block.</p>
              </article>
            ))}
          </div>
        </section>

        <section aria-labelledby="audience-pathways-title" className="content-stack">
          <h2 id="audience-pathways-title">Audience pathways</h2>
          <div className="content-grid content-grid--four">
            {audienceBlocks.map((block) => (
              <article className="content-card" key={block}>
                <h3>{block}</h3>
                <p className="muted-copy">Audience-specific entry point placeholder.</p>
              </article>
            ))}
          </div>
        </section>

        <section className="content-grid content-grid--two" aria-label="Support and trust blocks">
          <article className="content-card">
            <h2>Trust / support / deployment note</h2>
            <ul className="detail-list">
              <li>Sovereign deployment</li>
              <li>API-first</li>
              <li>Modular platform</li>
            </ul>
          </article>
          <article className="content-card">
            <h2>Footer</h2>
            <ul className="detail-list">
              <li>About</li>
              <li>Help</li>
              <li>Accessibility</li>
              <li>Legal</li>
              <li>Contact</li>
            </ul>
          </article>
        </section>
      </section>
    </main>
  );
}

