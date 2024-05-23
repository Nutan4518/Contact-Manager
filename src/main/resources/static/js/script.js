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
}

