const wordsSubmitForm = document.querySelector(".words-submit-form");
const wordsSubmitBtn = document.querySelector(".word-submit-btn");

wordsSubmitForm.addEventListener("submit", async e => {
    e.preventDefault();

    let wordsJson = {
        "words": []
    };

    const formData = new FormData(wordsSubmitForm);
    for (const entry of wordsSubmitForm) {
        if (entry.value !== "") {
            wordsJson["words"].push(entry.value);
        }
    }

    postData(wordsJson)
    .then((response) => response.text())
    .then((DOTstring) => {
        console.log(DOTstring);
        // create an array with nodes
        var parsedData = vis.parseDOTNetwork(DOTstring);

        // create a network
        var container = document.getElementById('mynetwork');

        // provide the data in the vis format
        var data = {
            nodes: parsedData.nodes,
            edges: parsedData.edges
        };
        
        var options = {
            autoResize: true,
            height: '100%',
            width: '100%',
            locale: 'en',
            clickToUse: false,
            // configure: {...},    // defined in the configure module.
            // edges: {...},        // defined in the edges module.
            // nodes: {...},        // defined in the nodes module.
            // groups: {...},       // defined in the groups module.
            // layout: {
            //     randomSeed: undefined,
            //     improvedLayout: false,
            //     clusterThreshold: 150,
            //     hierarchical: {
            //         enabled: false,
            //         levelSeparation: 150,
            //         nodeSpacing: 200,
            //         treeSpacing: 200,
            //         blockShifting: false,
            //         edgeMinimization: false,
            //         parentCentralization: false,
            //         direction: 'UD',        // UD, DU, LR, RL
            //         sortMethod: 'hubsize',  // hubsize, directed
            //         shakeTowards: 'leaves'  // roots, leaves
            //     }
            // },
            // interaction: {...},  // defined in the interaction module.
            // manipulation: {...}, // defined in the manipulation module.
            physics: {
                enabled: true,
                solver: "forceAtlas2Based",
                stabilization: {
                enabled: false // This is here just to see what's going on from the very beginning.
                }
            },      
        };
        // initialize your network!
        var network = new vis.Network(container, data, options);
    });
});

const BASE_URL = 'http://localhost:8080';

async function postData(data) {
    // Default options are marked with *
    const response = await fetch(BASE_URL + "/wordgraph", {
        method: "POST", // *GET, POST, PUT, DELETE, etc.
        mode: "cors", // no-cors, *cors, same-origin
        cache: "no-cache", // *default, no-cache, reload, force-cache, only-if-cached
        credentials: "same-origin", // include, *same-origin, omit
        headers: {
            "Content-Type": "application/json",
        },
        redirect: "follow", // manual, *follow, error
        referrerPolicy: "no-referrer", // no-referrer, *no-referrer-when-downgrade, origin, origin-when-cross-origin, same-origin, strict-origin, strict-origin-when-cross-origin, unsafe-url
        body: JSON.stringify(data), // body data type must match "Content-Type" header
    });
    return response; 
}
        