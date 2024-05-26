console.log("this is a script file")


function toggleSidebar() {
    var sidebar = document.getElementById("side-bar");
    var content = document.getElementById("base-content");

    if($('.side-bar').is(":visible")){

    //to hide display
        $(".side-bar").css("display","none");
        $(".base-content").css("margin-left","0%");
        }
        else{
    //to show display
        $(".side-bar").css("display","block");
            $(".base-content").css("margin-left","20%");
        }
};



document.addEventListener("DOMContentLoaded", function() {
    function search() {
        console.log("Search function called");
        var query = document.getElementById("search-input").value;

        if (query === '') {
//            console.log("blank input:", query);
            $(".search-result").hide();
        } else {
//            console.log("Search input:", query);

            // Sending request to server
            let url = `http://localhost:8282/search/${query}`;
            fetch(url).then((response) => {
                return response.json();
            }).then(data => {
//                console.log("data", data);
                let text = `<div class='list-group'>`;
                data.forEach(contact => {
                    text += `<a href="/user/${contact.cId}/contact/" class='list-group-item list-group-item-action'>${contact.name}</a>`;
                });
                text += `</div>`;
                $(".search-result").html(text);
                $(".search-result").show();
            }).catch(error => {
                console.error("Error fetching data:", error);
            });
        }
    }

    // Expose the function to the global scope
    window.search = search;
});
