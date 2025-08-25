console.log("✅ delete.js loaded successfully!");

function deleteMovie(movieId) {
    if (confirm(i18n.confirmDelete)) {
        fetch(`/admin/movies/${movieId}`, { method: "DELETE" })
            .then(r => {
                if (r.ok) {
                    // Server sẽ tự quyết định soft hay hard
                    r.json().then(res => {
                        if (res.type === "hard") {
                            alert(i18n.successHard);
                        } else {
                            alert(i18n.successSoft);
                        }
                        window.location.href = "/admin/movies";
                    }).catch(() => {
                        // Nếu server không trả JSON, fallback
                        alert(i18n.successDelete);
                        window.location.href = "/admin/movies";
                    });
                } else {
                    r.json().then(err =>
                        alert(i18n.errorDelete.replace("{0}", err.error || "Unknown error"))
                    );
                }
            })
            .catch(err => alert(i18n.errorNetwork.replace("{0}", err)));
    }
}
