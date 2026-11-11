
const menuItems = document.querySelectorAll(".sidebar nav a");
const pages = document.querySelectorAll(".page");

menuItems.forEach(item => {

    item.addEventListener("click", function(event) {

        event.preventDefault();

        // Hide all pages
        pages.forEach(page => {
            page.classList.add("hidden");
        });

        // Remove active class
        menuItems.forEach(link => {
            link.classList.remove("active");
        });

        // Show selected page
        const pageId = this.dataset.page;
        document.getElementById(pageId).classList.remove("hidden");

        // Highlight selected menu
        this.classList.add("active");

    });

});


const connectionForm = document.getElementById("connectionForm");

connectionForm.addEventListener("submit", function(event) {
    event.preventDefault();

    const host = document.getElementById("host").value;
    const port = document.getElementById("port").value;
    const database = document.getElementById("database").value;
    const username = document.getElementById("username").value;

    const message = document.getElementById("connectionMessage");

    if (!host || !port || !database || !username) {
        message.textContent = "Please fill all required fields!";
        message.style.color = "red";
        return;
    }

    message.textContent =
        "Form validated! Waiting for Spring Boot backend connection.";

    message.style.color = "#0284c7";
});
