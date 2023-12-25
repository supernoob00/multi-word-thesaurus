const wordsSubmitForm = document.querySelector(".words-submit-form");

wordsSubmitForm.addEventListener("submit", async e => {
    e.preventDefault();

    // create request body from words inside form inputs
    let wordsJson = {
        "words": []
    };
    const formData = new FormData(wordsSubmitForm);
    for (const entry of wordsSubmitForm) {
        if (entry.value !== "") {
            wordsJson["words"].push(entry.value);
        }
    }

    postWordsDto(wordsJson)
    .then((response) => response.json())
    .then((wordGraphData) => {
        console.log(wordGraphData);
        createNetwork(wordGraphData);
    });
});

function createNetwork(wordGraphData) {
    const dotString = wordGraphData.graph;
    // create an array with nodes
    const parsedData = vis.parseDOTNetwork(dotString);
    // create a network
    const container = document.getElementById('mynetwork');
    // provide the data in the vis format
    const data = {
        nodes: parsedData.nodes,
        edges: parsedData.edges
    };

    const options = {
        autoResize: true,
        height: '100%',
        width: '100%',
        locale: 'en',
        clickToUse: false,
        // configure: {...},    // defined in the configure module.
        // edges: {...},        // defined in the edges module.
        nodes: {
            borderWidth: 0
        },        // defined in the nodes module.
        // groups: {...},       // defined in the groups module.
        layout: {// layout: {
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
            randomSeed: undefined,
            improvedLayout: true,
            clusterThreshold: 150,
            hierarchical: {
                enabled: false,
                levelSeparation: 150,
                nodeSpacing: 200,
                treeSpacing: 200,
                blockShifting: false,
                edgeMinimization: false,
                parentCentralization: false,
                direction: 'UD',        // UD, DU, LR, RL
                sortMethod: 'hubsize',  // hubsize, directed
                shakeTowards: 'leaves'  // roots, leaves
            }
        },
        interaction: {
            hover: true
        },
        // manipulation: {...}, // defined in the manipulation module.
        physics: {
            enabled: true,
            solver: "forceAtlas2Based",
            stabilization: {
                enabled: false // This is here just to see what's going on from the very beginning.
            }
        },
    };
    const network = new vis.Network(container, data, options);

    network.on("hoverNode", function (params) {
        network.canvas.body.container.style.cursor = 'pointer'
    });

    network.on("blurNode", function (params) {
        network.canvas.body.container.style.cursor = 'default'
    });

    network.on('click', function(properties) {
        const nodeId = network.getNodeAt({x:properties.event.srcEvent.offsetX, y:properties.event.srcEvent.offsetY});
        console.log(nodeId);
    });
}

function displaySynonyms(synonymList) {
    const box = document.createElement("div");
    for (const synonym of synonymList.synonyms) {
        const span = document.createElement("span");
        span.textContent = synonym;
        box.appendChild(span);
    }
}

const BASE_URL = 'http://localhost:8080';

async function postWordsDto(wordsDto) {
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
        body: JSON.stringify(wordsDto), // body data type must match "Content-Type" header
    });
    return response;
}

async function getSynonymList(word) {
    // Default options are marked with *
    const response = await fetch(BASE_URL + `/words/${word}`, {
        method: "GET", // *GET, POST, PUT, DELETE, etc.
        mode: "cors", // no-cors, *cors, same-origin
        cache: "no-cache", // *default, no-cache, reload, force-cache, only-if-cached
        credentials: "same-origin", // include, *same-origin, omit
        headers: {
        },
        redirect: "follow", // manual, *follow, error
        referrerPolicy: "no-referrer", // no-referrer, *no-referrer-when-downgrade, origin, origin-when-cross-origin, same-origin, strict-origin, strict-origin-when-cross-origin, unsafe-url
    });
    return response;
}


        