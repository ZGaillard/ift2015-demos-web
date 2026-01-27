(() => {
    const STORAGE_TOC = "ift-hide-toc";

    const readState = () => {
        try {
            return localStorage.getItem(STORAGE_TOC) === "1";
        } catch (err) {
            return false;
        }
    };

    const writeState = (hidden) => {
        try {
            localStorage.setItem(STORAGE_TOC, hidden ? "1" : "0");
        } catch (err) {
            // Ignore storage errors.
        }
    };

    const applyState = (hidden, button) => {
        document.body.classList.toggle("ift-hide-toc", hidden);
        button.textContent = hidden ? "Afficher sommaire" : "Masquer sommaire";
        button.setAttribute("aria-pressed", hidden ? "true" : "false");
    };

    const init = () => {
        const content = document.querySelector(".md-content__inner");
        const toc = document.querySelector(".md-sidebar--secondary");
        if (!content || !toc) return;

        const bar = document.createElement("div");
        bar.className = "ift-toc-toggle";

        const button = document.createElement("button");
        button.type = "button";
        button.className = "ift-toggle-btn";
        button.setAttribute("aria-pressed", "false");

        bar.appendChild(button);
        content.prepend(bar);

        const hidden = readState();
        applyState(hidden, button);

        button.addEventListener("click", () => {
            const nextHidden = !document.body.classList.contains("ift-hide-toc");
            writeState(nextHidden);
            applyState(nextHidden, button);
        });
    };

    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", init, { once: true });
    } else {
        init();
    }
})();
