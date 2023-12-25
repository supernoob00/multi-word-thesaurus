import {postWordsDto, getSynonymList} from "./service.js";

const wordsSubmitForm = document.querySelector(".words-submit-form");
wordsSubmitForm.addEventListener("submit", async e => {
    e.preventDefault();

    removeSynonymsDisplay();

    // create request body from words inside form inputs
    let wordsJson = {
        "words": []
    };
    const formData = new FormData(wordsSubmitForm);
    for (const entry of wordsSubmitForm) {
        if (entry.value !== "") {
            const formattedValue = entry.value.trim();
            wordsJson["words"].push(formattedValue);
        }
    }

    postWordsDto(wordsJson)
        .then((response) => response.json())
        .then((wordGraphData) => {
            console.log(wordGraphData);
            changeNetworkData(wordGraphData);
        });
});

const network = createNetwork();

function createNetwork() {
    const container = document.getElementById('mynetwork');

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
    const network = new vis.Network(container, null, options);

    network.on("hoverNode", function (params) {
        network.canvas.body.container.style.cursor = 'pointer'
    });

    network.on("blurNode", function (params) {
        network.canvas.body.container.style.cursor = 'default'
    });

    network.on("click", function () {
        removeSynonymsDisplay();
    });

    network.addEventListener('doubleClick', function(properties) {
        const nodeId = network.getNodeAt({x:properties.event.srcEvent.offsetX, y:properties.event.srcEvent.offsetY});
        if (nodeId === undefined) {
            return;
        }
        console.log(nodeId);
        getSynonymList(nodeId)
            .then(response => {
                if (response.status === 404) {
                    return Promise.reject("404 Not Found; no synonyms exist for given word");
                } else {
                    return response.json();
                }
            })
            .then(synonymList => {
                displaySynonyms(synonymList);
            })
            .catch(error => {
                console.log(error);
                displayEmptySynonyms(nodeId);
            });
    });
    return network;
}

function changeNetworkData(wordGraphData) {
    const dotString = wordGraphData.graph;
    // create an array with nodes
    const parsedData = vis.parseDOTNetwork(dotString);
    const data = {
        nodes: parsedData.nodes,
        edges: parsedData.edges
    };
    network.setData(data);
}

function displaySynonyms(synonymList) {
    const box = document.createElement("div");
    box.className = "synonyms-container";
    for (const synonym of synonymList.synonyms) {
        const span = document.createElement("span");
        span.textContent = synonym;
        box.appendChild(span);
    }
    document.body.appendChild(box);
}

function displayEmptySynonyms(word) {
    const box = document.createElement("div");
    box.className = "synonyms-container-empty";
    const span = document.createElement("span");
    span.textContent = `No synonyms were found for ${word}`;
    box.appendChild(span);
    document.body.appendChild(box);
}

function removeSynonymsDisplay() {
    const synonymDisplay = document.body.querySelector(".synonyms-container");
    const emptySynonymDisplay = document.body.querySelector(".empty-synonyms-container");
    if (synonymDisplay !== null) {
        document.body.removeChild(synonymDisplay);
    } else if (emptySynonymDisplay !== null) {
        document.body.removeChild(emptySynonymDisplay);
    }
}




        