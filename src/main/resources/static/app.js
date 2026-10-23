const API_BASE = "http://localhost:8080/api/v1";

console.log("MY NEW APP.JS IS LOADED");
const FALLBACK_MOVIES = [
    {
        id: 1,
        title: "Dune: Part Two",
        language: "English",
        genre: "Sci-Fi / Adventure",
        certificate: "UA",
        posterUrl: "./images/dune_part_2.jpg"
    },
    {
        id: 2,
        title: "Monkey Man",
        language: "English",
        genre: "Action / Thriller",
        certificate: "A",
        posterUrl: "./images/monkey-man.jpg"
    },
    {
        id: 3,
        title: "Kung Fu Panda 4",
        language: "English",
        genre: "Animation / Comedy",
        certificate: "U",
        posterUrl: "./images/Kungfu_panda_4.jpg"
    },
    {
        id: 4,
        title: "Animal",
        language: "Hindi",
        genre: "Action / Thriller",
        certificate: "A",
        posterUrl: "./images/Animal.jpg"
    },
    {
        id: 5,
        title: "Mulshi Pattern",
        language: "Marathi",
        genre: "Action/Thriller",
        certificate: "UA",
        posterUrl: "./images/mulshi-pattern.jpg"
    },
    {
            id: 6,
            title: "Jwan",
            language: "Hindi",
            genre: "Action/Thriller",
            certificate: "UA",
            posterUrl: "./images/jawan.jpg"
        }
];

const savedToken = localStorage.getItem("showtimeToken");

// a saved profile without a token is an old-style login, so ignore it
const savedProfile = savedToken
    ? JSON.parse(localStorage.getItem("showtimeProfile") || "null")
    : null;

const state = {
    movies: [],
    shows: [],
    selectedShow: null,
    selectedSeats: [],
    selectedDate: dateKey(new Date(Date.now() + 86400000)),
    activeFilter: "All",
    profile: savedProfile,
    token: savedToken,
    pendingShowId: null
};

const $ = selector => document.querySelector(selector);


/* =========================================================
   HELPERS
========================================================= */

function dateKey(date) {
    return date.toISOString().slice(0, 10);
}

function formatTime(value) {
    return new Date(value).toLocaleTimeString([], {
        hour: "numeric",
        minute: "2-digit"
    });
}

function money(value) {
    return `₹${Number(value).toLocaleString("en-IN")}`;
}

function escapeHtml(value = "") {
    return String(value).replace(
        /[&<>'"]/g,
        character => ({
            "&": "&amp;",
            "<": "&lt;",
            ">": "&gt;",
            "'": "&#39;",
            '"': "&quot;"
        }[character])
    );
}

function normalizePhone(value) {
    return String(value || "").replace(/\D/g, "");
}


/* =========================================================
   API
========================================================= */

async function fetchJson(path, options = {}) {
    const headers = { "Content-Type": "application/json" };

    // Send the token only to protected endpoints. The server rejects an expired token
    // even on public URLs, which would break browsing movies after it expires.
    const needsAuth = path.startsWith("/bookings") ||
        path.startsWith("/profiles/me") ||
        path.startsWith("/admin");

    if (needsAuth && state.token) {
        headers["Authorization"] = `Bearer ${state.token}`;
    }

    const response = await fetch(`${API_BASE}${path}`, {
        ...options,
        headers
    });

    if (!response.ok) {
        if (response.status === 401 && needsAuth) {
            clearSession();   // token expired or invalid: log the user out
        }

        const error = await response.json().catch(() => ({}));
        throw new Error(error.message || "Something went wrong");
    }

    return response.json();
}

function setSession(auth) {
    state.token = auth.accessToken;
    localStorage.setItem("showtimeToken", auth.accessToken);
    setProfile(auth.profile);
}

function clearSession() {
    state.token = null;
    state.profile = null;

    localStorage.removeItem("showtimeToken");
    localStorage.removeItem("showtimeProfile");

    renderProfile();
}


/* =========================================================
   MOVIES
========================================================= */

function renderMovies() {
    const query = $("#searchInput").value.toLowerCase().trim();

    const movies = state.movies.filter(movie => {
        const searchableText =
            `${movie.title} ${movie.genre} ${movie.language}`.toLowerCase();

        const matchesQuery = searchableText.includes(query);

        const matchesFilter =
            state.activeFilter === "All" ||
            movie.language === state.activeFilter ||
            movie.genre.includes(state.activeFilter);

        return matchesQuery && matchesFilter;
    });

    $("#movieGrid").innerHTML = movies.length
        ? movies.map((movie, index) => `
            <article
                class="movie-card"
                style="animation-delay:${index * 90}ms"
            >
                <div class="poster-wrap">
                    <img
                        src="${escapeHtml(movie.posterUrl)}"
                        alt="${escapeHtml(movie.title)} poster"
                        loading="lazy"
                    >

                    <span class="poster-badge">
                        ${escapeHtml(movie.certificate || "NOW SHOWING")}
                    </span>
                </div>

                <div class="movie-info">
                    <div>
                        <h3>${escapeHtml(movie.title)}</h3>
                        <p>
                            ${escapeHtml(movie.language)}
                            ·
                            ${escapeHtml(movie.genre)}
                        </p>
                    </div>

                    <div class="movie-rating">
                        <span>★</span> 8.${index + 1}
                    </div>
                </div>

                <button
                    class="movie-action"
                    data-movie="${escapeHtml(movie.title)}"
                    type="button"
                >
                    Find showtimes
                    <span>↗</span>
                </button>
            </article>
        `).join("")
        : `
            <div class="empty-state">
                <strong>No movies found</strong>
                <p>Try a different search or filter.</p>
            </div>
        `;

    document.querySelectorAll(".movie-action").forEach(button => {
        button.addEventListener("click", () => {
            $("#showtimesSection").scrollIntoView({
                behavior: "smooth"
            });

            const target = state.shows.find(
                show => show.movie.title === button.dataset.movie
            );

            if (target) {
                openSeatModal(target.id);
            } else {
                showToast(
                    "Choose a showtime below to book this movie.",
                    false
                );
            }
        });
    });
}


/* =========================================================
   HERO
========================================================= */

function renderHero() {
    const movie = state.movies[0];

    if (!movie) return;

    $("#heroImage").src = movie.posterUrl;
    $("#heroImage").alt = `${movie.title} artwork`;

    $("#heroTitle").innerHTML = `
        ${escapeHtml(movie.title)}
        <br>
        <em>on the biggest screen.</em>
    `;

    $("#heroDescription").textContent =
        movie.description ||
        "Big screens, first-day energy, and a seat with your name on it.";

    $("#heroLanguage").textContent = movie.language;
    $("#heroGenre").textContent = movie.genre;
    $("#heroCount").textContent =
        String(state.movies.length).padStart(2, "0");
}


/* =========================================================
   DATES
========================================================= */

function renderDates() {
    const dates = Array.from(
        { length: 5 },
        (_, index) =>
            new Date(Date.now() + (index + 1) * 86400000)
    );

    $("#datePicker").innerHTML = dates.map(date => {
        const key = dateKey(date);
        const day = indexDay(date);

        return `
            <button
                class="date-button ${key === state.selectedDate ? "active" : ""}"
                data-date="${key}"
                type="button"
            >
                <span>${day}</span>
                <strong>${date.getDate()}</strong>
            </button>
        `;
    }).join("");

    document.querySelectorAll(".date-button").forEach(button => {
        button.addEventListener("click", () => {
            state.selectedDate = button.dataset.date;

            renderDates();

            loadShows();
        });
    });
}

function indexDay(date) {
    return date.toLocaleDateString([], {
        weekday: "short"
    });
}


/* =========================================================
   SHOWTIMES
========================================================= */

function renderShows() {
    const grouped = state.shows.reduce((groups, show) => {
        const key = show.movie.id;

        if (!groups[key]) {
            groups[key] = {
                movie: show.movie,
                theatre: show.theatre,
                shows: []
            };
        }

        groups[key].shows.push(show);

        return groups;
    }, {});

    const entries = Object.values(grouped);

    $("#showList").innerHTML = entries.length
        ? entries.map(group => `
            <article class="show-row">

                <div class="show-movie">
                    <img
                        class="mini-poster"
                        src="${escapeHtml(group.movie.posterUrl)}"
                        alt=""
                    >

                    <div>
                        <h3>
                            ${escapeHtml(group.movie.title)}
                        </h3>

                        <p>
                            ${escapeHtml(group.movie.language)}
                            ·
                            ${escapeHtml(group.movie.durationMinutes)}
                            min
                        </p>
                    </div>
                </div>

                <div class="show-theatre">
                    <strong>
                        ${escapeHtml(group.theatre.name)}
                    </strong>

                    <p class="theatre-address">
                        ${escapeHtml(group.theatre.address)}
                    </p>
                </div>

                <div class="show-times">
                    ${group.shows.map(show => `
                        <button
                            class="time-button"
                            data-show-id="${show.id}"
                            type="button"
                        >
                            ${formatTime(show.startsAt)}
                            ·
                            ${money(show.ticketPrice)}
                        </button>
                    `).join("")}
                </div>

            </article>
        `).join("")
        : `
            <div class="empty-state">
                <strong>No shows available</strong>
                <p>No shows found for this date. Try another day.</p>
            </div>
        `;

    document.querySelectorAll(".time-button").forEach(button => {
        button.addEventListener("click", () => {
            openSeatModal(Number(button.dataset.showId));
        });
    });
}


/* =========================================================
   LOAD DATA
========================================================= */

async function loadMovies() {
    try {
        state.movies = await fetchJson("/movies");
    } catch (error) {
        state.movies = FALLBACK_MOVIES;

        showToast(
            "Showing sample movies. Start the backend to book tickets.",
            false
        );
    }

    renderHero();
    renderMovies();
}

async function loadShows() {
    const city = encodeURIComponent($("#citySelect").value);

    $("#showList").innerHTML = `
        <div class="loading-state">
            <span class="loader"></span>
            Finding shows in
            ${escapeHtml($("#citySelect").value)}...
        </div>
    `;

    try {
        state.shows = await fetchJson(
            `/shows?city=${city}&date=${state.selectedDate}`
        );

        renderShows();
    } catch (error) {
        state.shows = [];

        $("#showList").innerHTML = `
            <div class="empty-state">
                <strong>Shows unavailable</strong>
                <p>
                    Connect the Spring Boot backend
                    to see live showtimes.
                </p>
            </div>
        `;
    }
}


/* =========================================================
   SEAT SELECTION
========================================================= */

function openSeatModal(showId) {
    if (!state.profile) {
        state.pendingShowId = showId;

        openAccountModal();

        showToast(
            "Create a profile or log in before choosing seats.",
            false
        );

        return;
    }

    state.selectedShow = state.shows.find(
        show => show.id === showId
    );

    state.selectedSeats = [];

    $("#modalTitle").textContent = "Select your seats";

    $("#modalSubtitle").textContent =
        `${state.selectedShow.movie.title} · ` +
        `${state.selectedShow.theatre.name} · ` +
        `${formatTime(state.selectedShow.startsAt)}`;

    renderSeats();

    $("#bookingModal").hidden = false;

    document.body.style.overflow = "hidden";
}

function renderSeats() {
    const seatLabels = Array.from(
        {
            length: state.selectedShow.totalSeats
        },
        (_, index) =>
            index < 10
                ? `A${index + 1}`
                : `B${index - 9}`
    );

    const availableLabels = new Set(
        state.selectedShow.availableSeatLabels || seatLabels
    );

    $("#seatGrid").innerHTML = seatLabels.map(label => {
        const occupied = !availableLabels.has(label);
        const selected = state.selectedSeats.includes(label);

        return `
            <button
                class="seat ${occupied ? "occupied" : ""} ${selected ? "selected" : ""}"
                ${occupied ? "disabled" : ""}
                data-seat="${label}"
                type="button"
            >
                ${label}
            </button>
        `;
    }).join("");

    document
        .querySelectorAll(".seat:not(.occupied)")
        .forEach(button => {
            button.addEventListener("click", () => {
                const label = button.dataset.seat;

                state.selectedSeats =
                    state.selectedSeats.includes(label)
                        ? state.selectedSeats.filter(
                            seat => seat !== label
                        )
                        : [
                            ...state.selectedSeats,
                            label
                        ];

                renderSeats();
            });
        });

    const total =
        state.selectedSeats.length *
        Number(state.selectedShow.ticketPrice);

    $("#totalAmount").textContent = money(total);

    $("#continueButton").disabled =
        !state.selectedSeats.length;
}


/* =========================================================
   MODALS & TOASTS
========================================================= */

function closeModals() {
    $("#bookingModal").hidden = true;
    $("#customerModal").hidden = true;
    $("#accountModal").hidden = true;
    $("#adminModal").hidden = true;

    document.body.style.overflow = "";
}

function showToast(message, success = true) {
    const toast = $("#successToast");

    toast.textContent = message;

    toast.style.borderLeftColor =
        success ? "#48bd8c" : "#f5c84b";

    toast.classList.remove("visible");

    requestAnimationFrame(() => {
        toast.classList.add("visible");
    });

    setTimeout(() => {
        toast.classList.remove("visible");
    }, 5000);
}


/* =========================================================
   PROFILE
========================================================= */

function openAccountModal() {
    $("#accountModal").hidden = false;

    document.body.style.overflow = "hidden";

    renderProfile();
}

function renderProfile() {
    const profile = state.profile;

    updateAdminButton();

    $("#profileAuth").hidden = Boolean(profile);
    $("#profileView").hidden = !profile;

    if (!profile) return;

    $("#profileDisplayName").textContent = profile.name;

    $("#profileDisplayContact").textContent =
        `${profile.email} · ${profile.phone}`;

    loadBookingHistory();
}

function setProfile(profile) {
    state.profile = profile;

    localStorage.setItem(
        "showtimeProfile",
        JSON.stringify(profile)
    );

    renderProfile();

    if (state.pendingShowId) {
        const showId = state.pendingShowId;

        state.pendingShowId = null;

        closeModals();

        openSeatModal(showId);
    }
}


/* =========================================================
   BOOKING HISTORY
========================================================= */

function renderBookingHistory(bookings) {
    $("#bookingHistory").innerHTML = bookings.length
        ? bookings.map(booking => `
            <article
                class="booking-card ${
                    booking.status === "CANCELLED"
                        ? "cancelled"
                        : ""
                }"
            >

                <div class="booking-card-heading">
                    <strong>
                        ${escapeHtml(booking.movieTitle)}
                    </strong>

                    <span class="booking-status">
                        ${escapeHtml(booking.status)}
                    </span>
                </div>

                <p>
                    ${escapeHtml(booking.theatreName)}
                    ·
                    ${formatTime(booking.bookedAt)}
                </p>

                <div class="booking-card-meta">
                    <span>
                        Seats
                        <strong>
                            ${escapeHtml(
                                booking.seatLabels.join(", ")
                            )}
                        </strong>
                    </span>

                    <strong>
                        ${money(booking.totalAmount)}
                    </strong>
                </div>

                ${
                    booking.status === "CONFIRMED"
                        ? `
                            <button
                                class="cancel-booking"
                                data-booking-id="${booking.id}"
                                type="button"
                            >
                                Cancel booking
                            </button>
                        `
                        : ""
                }

            </article>
        `).join("")
        : `
            <div class="empty-state booking-empty">
                <strong>No bookings found</strong>
                <p>No bookings found for this number.</p>
            </div>
        `;

    document
        .querySelectorAll(".cancel-booking")
        .forEach(button => {
            button.addEventListener("click", () => {
                cancelBooking(
                    Number(button.dataset.bookingId)
                );
            });
        });
}

async function loadBookingHistory() {
    if (!state.profile) return;

    $("#bookingHistory").innerHTML = `
        <div class="loading-state">
            <span class="loader"></span>
            Finding your bookings...
        </div>
    `;

    try {
        const bookings = await fetchJson(
            "/profiles/me/bookings"
        );

        renderBookingHistory(bookings);
    } catch (error) {
        $("#bookingHistory").innerHTML = `
            <p class="form-error">
                ${escapeHtml(
                    error.message ||
                    "Unable to load bookings"
                )}
            </p>
        `;
    }
}

async function cancelBooking(bookingId) {
    if (
        !state.profile ||
        !window.confirm(
            "Cancel this booking and release the seats?"
        )
    ) {
        return;
    }

    try {
        await fetchJson(
            `/bookings/${bookingId}/cancel`,
            {
                method: "POST"
            }
        );

        showToast(
            "Booking cancelled and seats released."
        );

        await loadBookingHistory();

        loadShows();
    } catch (error) {
        showToast(
            error.message || "Unable to cancel booking",
            false
        );
    }
}


/* =========================================================
   SEARCH & FILTERS
========================================================= */

$("#searchInput").addEventListener(
    "input",
    renderMovies
);

document
    .querySelectorAll(".filter-button")
    .forEach(button => {
        button.addEventListener("click", () => {
            state.activeFilter = button.dataset.filter;

            document
                .querySelectorAll(".filter-button")
                .forEach(item => {
                    item.classList.toggle(
                        "active",
                        item === button
                    );
                });

            renderMovies();
        });
    });


/* =========================================================
   NAVIGATION
========================================================= */

$("#citySelect").addEventListener(
    "change",
    loadShows
);

$("#heroBrowseButton").addEventListener(
    "click",
    () => {
        $("#moviesSection").scrollIntoView({
            behavior: "smooth"
        });
    }
);

$("#viewAllButton").addEventListener(
    "click",
    () => {
        $("#searchInput").value = "";

        state.activeFilter = "All";

        document
            .querySelectorAll(".filter-button")
            .forEach(item => {
                item.classList.toggle(
                    "active",
                    item.dataset.filter === "All"
                );
            });

        renderMovies();

        $("#moviesSection").scrollIntoView({
            behavior: "smooth"
        });
    }
);


/* =========================================================
   MODAL CONTROLS
========================================================= */

$("#closeModalButton").addEventListener(
    "click",
    closeModals
);

$("#closeCustomerButton").addEventListener(
    "click",
    closeModals
);

$("#closeAccountButton").addEventListener(
    "click",
    closeModals
);

$("#profileButton").addEventListener(
    "click",
    openAccountModal
);


/* =========================================================
   PROFILE TABS
========================================================= */

$("#createTab").addEventListener(
    "click",
    () => {
        $("#createTab").classList.add("active");
        $("#loginTab").classList.remove("active");

        $("#profileCreateForm").hidden = false;
        $("#profileLoginForm").hidden = true;
    }
);

$("#loginTab").addEventListener(
    "click",
    () => {
        $("#loginTab").classList.add("active");
        $("#createTab").classList.remove("active");

        $("#profileCreateForm").hidden = true;
        $("#profileLoginForm").hidden = false;
    }
);


/* =========================================================
   CREATE PROFILE
========================================================= */

$("#profileCreateForm").addEventListener(
    "submit",
    async event => {
        event.preventDefault();

        const error = $("#profileCreateError");

        error.textContent = "";

        try {
            const email = $("#profileEmail").value;
            const password = $("#profilePassword").value;

            await fetchJson(
                "/profiles",
                {
                    method: "POST",

                    body: JSON.stringify({
                        name: $("#profileName").value,
                        email,
                        phone: $("#profilePhone").value,
                        password
                    })
                }
            );

            // profile created, now log in to get a token
            const auth = await fetchJson(
                "/auth/login",
                {
                    method: "POST",
                    body: JSON.stringify({
                        identifier: email,
                        password
                    })
                }
            );

            setSession(auth);

            showToast(
                `Welcome, ${auth.profile.name}. Your profile is ready.`
            );
        } catch (profileError) {
            error.textContent =
                profileError.message;
        }
    }
);


/* =========================================================
   LOGIN
========================================================= */

$("#profileLoginForm").addEventListener(
    "submit",
    async event => {
        event.preventDefault();

        const error = $("#profileLoginError");

        error.textContent = "";

        try {
            const auth = await fetchJson(
                "/auth/login",
                {
                    method: "POST",
                    body: JSON.stringify({
                        identifier: $("#profileIdentifier").value,
                        password: $("#loginPassword").value
                    })
                }
            );

            setSession(auth);

            showToast(
                `Welcome back, ${auth.profile.name}.`
            );
        } catch (profileError) {
            error.textContent =
                profileError.message;
        }
    }
);


/* =========================================================
   LOGOUT
========================================================= */

$("#logoutButton").addEventListener(
    "click",
    () => {
        clearSession();

        showToast(
            "You have been logged out."
        );
    }
);


/* =========================================================
   CONTINUE TO CUSTOMER FORM
========================================================= */

$("#continueButton").addEventListener(
    "click",
    () => {
        $("#bookingModal").hidden = true;

        $("#formSeats").textContent =
            `${state.selectedSeats.join(", ")} · ` +
            `${state.selectedSeats.length} seat${
                state.selectedSeats.length > 1
                    ? "s"
                    : ""
            }`;

        $("#formTotal").textContent = money(
            state.selectedSeats.length *
            Number(state.selectedShow.ticketPrice)
        );

        $("#bookingProfileLabel").textContent =
            `Booking as ${state.profile.name} · ` +
            `${state.profile.email}`;

        $("#customerModal").hidden = false;
    }
);


/* =========================================================
   COMPLETE BOOKING
========================================================= */

$("#customerForm").addEventListener(
    "submit",
    async event => {
        event.preventDefault();

        const error = $("#bookingError");

        error.textContent = "";

        const submit =
            event.target.querySelector(
                "button[type=submit]"
            );

        submit.disabled = true;

        try {
            const booking = await fetchJson(
                `/bookings/shows/${state.selectedShow.id}`,
                {
                    method: "POST",

                    body: JSON.stringify({
                        seatLabels: state.selectedSeats
                    })
                }
            );

            closeModals();

            event.target.reset();

            showToast(
                `Booking #${booking.id} confirmed. ` +
                `Your seats are ${booking.seatLabels.join(", ")}.`
            );

            loadShows();
        } catch (bookingError) {
            error.textContent =
                bookingError.message;
        } finally {
            submit.disabled = false;
        }
    }
);


/* =========================================================
   KEYBOARD SUPPORT
========================================================= */

document.addEventListener(
    "keydown",
    event => {
        if (event.key === "Escape") {
            closeModals();
        }
    }
);


/* =========================================================
   ADMIN PANEL
========================================================= */

// the button is only a convenience: the server checks the ADMIN role on every /admin call
function updateAdminButton() {
    $("#adminButton").hidden = !(
        state.profile && state.profile.role === "ADMIN"
    );
}

async function openAdminModal() {
    $("#adminModal").hidden = false;

    document.body.style.overflow = "hidden";

    await loadAdminOptions();
}

// fills the movie and theatre dropdowns of the "Add show" form
async function loadAdminOptions() {
    const city = $("#citySelect").value;

    $("#showMovie").innerHTML = state.movies
        .map(movie =>
            `<option value="${movie.id}">${escapeHtml(movie.title)}</option>`
        )
        .join("");

    try {
        const theatres = await fetchJson(
            `/theatres?city=${encodeURIComponent(city)}`
        );

        $("#showTheatre").innerHTML = theatres.length
            ? theatres
                .map(theatre =>
                    `<option value="${theatre.id}">${escapeHtml(theatre.name)}</option>`
                )
                .join("")
            : `<option value="">No theatres in ${escapeHtml(city)} yet</option>`;
    } catch (error) {
        $("#showTheatre").innerHTML =
            `<option value="">Could not load theatres</option>`;
    }
}

// one helper used by all three admin forms
async function submitAdmin(event, path, body, errorSelector, successText, after) {
    event.preventDefault();

    const error = $(errorSelector);

    error.textContent = "";

    try {
        const result = await fetchJson(
            path,
            {
                method: "POST",
                body: JSON.stringify(body)
            }
        );

        event.target.reset();

        showToast(successText(result));

        if (after) await after();
    } catch (adminError) {
        error.textContent = adminError.message;
    }
}

$("#adminButton").addEventListener("click", openAdminModal);

$("#closeAdminButton").addEventListener("click", closeModals);

document.querySelectorAll("[data-admin-tab]").forEach(tab => {
    tab.addEventListener("click", () => {
        document.querySelectorAll("[data-admin-tab]")
            .forEach(t => t.classList.toggle("active", t === tab));

        ["adminMovieForm", "adminTheatreForm", "adminShowForm"]
            .forEach(id => {
                $("#" + id).hidden = id !== tab.dataset.adminTab;
            });
    });
});

$("#adminMovieForm").addEventListener("submit", event =>
    submitAdmin(
        event,
        "/admin/movies",
        {
            title: $("#movieTitle").value.trim(),
            language: $("#movieLanguage").value.trim(),
            genre: $("#movieGenre").value.trim(),
            durationInMin: Number($("#movieDuration").value),
            certificate: $("#movieCertificate").value.trim(),
            posterUrl: $("#moviePoster").value.trim() || null,
            description: $("#movieDescription").value.trim()
        },
        "#adminMovieError",
        movie => `Movie "${movie.title}" added.`,
        async () => {
            await loadMovies();
            await loadAdminOptions();
        }
    )
);

$("#adminTheatreForm").addEventListener("submit", event =>
    submitAdmin(
        event,
        "/admin/theatres",
        {
            name: $("#theatreName").value.trim(),
            city: $("#theatreCity").value,
            address: $("#theatreAddress").value.trim()
        },
        "#adminTheatreError",
        theatre => `Theatre "${theatre.name}" added in ${theatre.city}.`,
        loadAdminOptions
    )
);

$("#adminShowForm").addEventListener("submit", event =>
    submitAdmin(
        event,
        "/admin/shows",
        {
            movieId: Number($("#showMovie").value),
            theatreId: Number($("#showTheatre").value),
            startsAt: $("#showStart").value,
            ticketPrice: Number($("#showPrice").value),
            rows: Number($("#showRows").value),
            seatsPerRow: Number($("#showSeats").value)
        },
        "#adminShowError",
        () => "Show added with all its seats.",
        loadShows
    )
);


/* =========================================================
   INITIALIZE APP
========================================================= */

renderDates();
updateAdminButton();
loadMovies();
loadShows();