import {postWordsDto, getSynonymList} from "./service.js";

/* show example network if first-time visitor (no cookie exists) */
window.onload = function() {
    const now = new Date().getTime();
    const visitTime = localStorage.getItem("visit_timestamp");
    if (now - visitTime > 7200000) {
        localStorage.setItem("visit_timestamp", "now");

        wordsSubmitFormInputs[0].value = "happy";
        wordsSubmitFormInputs[1].value = "sad";
        wordsSubmitFormInputs[2].value = "tasty";
        wordsSubmitFormInputs[3].value = "wizard";
        wordsSubmitFormInputs[4].value = "rock";

        wordsSubmitForm.requestSubmit();
    }
}

const network = createNetwork();
let addedWords = new Set;

const leftSideContainer = document.body.querySelector(".left-side-container");
const wordsSubmitForm = document.querySelector(".words-submit-form");
const wordsSubmitFormInputs = wordsSubmitForm.querySelectorAll("input");
const clearFormBtn = document.querySelector(".clear-form-btn");
const expandFormBtn = document.querySelector(".expand-form-btn");
const collapseFormBtn = document.querySelector(".collapse-form-btn");
const networkInfoPanel = document.querySelector(".network-info-panel");
const hideAllCountsBtn = document.querySelector(".hide-counts-btn");

/* bind event listeners to various elements */
wordsSubmitForm.addEventListener("submit", async e => {
    e.preventDefault();

    removeSynonymsDisplay();

    // create request body from words inside form inputs
    let wordsJson = {
        "words": []
    };
    const formData = new FormData(wordsSubmitForm);
    for (const entry of wordsSubmitFormInputs) {
        entry.value = entry.value.trim();
        if (entry.value !== "" && !entry.classList.contains("hidden")) {
            const formattedValue = entry.value;
            wordsJson["words"].push(formattedValue);
        }
    }

    // send POST request to server endpoint, then update network using response
    postWordsDto(wordsJson)
        .then((response) => response.json())
        .then((wordGraphData) => {
            console.log(wordGraphData);
            changeNetworkData(wordGraphData);
            updateNetworkInfoPanel(wordGraphData);
            updateAddedWords(wordGraphData);
        });

    // shift text in form inputs upward to remove any empty spaces in-between
    let i = 0;
    for (const input of wordsSubmitFormInputs) {
        if (input.value !== "" && !input.classList.contains("hidden")) {
            console.log(input.value);
            wordsSubmitFormInputs[i].value = input.value;
            i++;
        }
    }
    for (; i < wordsSubmitFormInputs.length; i++) {
        wordsSubmitFormInputs[i].value = "";
    }
});
clearFormBtn.addEventListener("click", event => {
    event.preventDefault();
    wordsSubmitForm.reset();
});
expandFormBtn.addEventListener("click", event => {
    event.preventDefault();
    expandWordsSubmitForm();

    const inputs = document.querySelectorAll(
        ".words-submit-form > input");
    if (!inputs[14].classList.contains("hidden")) {
        expandFormBtn.classList.add("hidden");
    } else {
        expandFormBtn.textContent = "Even More Words";
    }
});
collapseFormBtn.addEventListener("click", event => {
    event.preventDefault();

    const start = !wordsSubmitFormInputs[14].classList.contains("hidden") ? 10 : 5;
    for (let i = start; i < start + 5; i++) {
        wordsSubmitFormInputs[i].classList.add("hidden");
    }
    if (wordsSubmitFormInputs[5].classList.contains("hidden")) {
        console.log("fail");
        collapseFormBtn.classList.add("hidden");
        expandFormBtn.textContent = "More Words";
    } else {
        expandFormBtn.textContent = "Even More Words";
    }
    expandFormBtn.classList.remove("hidden");
});
hideAllCountsBtn.addEventListener("click", event => {
    const display = document.querySelector(".complete-node-info");
    if (display.classList.contains("hidden")) {
        display.classList.remove("hidden");
        hideAllCountsBtn.textContent = "Hide";
    } else {
        display.classList.add("hidden");
        hideAllCountsBtn.textContent = "Show";
    }
});

function expandWordsSubmitForm() {
    const inputs = document.querySelectorAll(
        ".words-submit-form > input");

    let start = inputs[5].classList.contains("hidden") ? 5 : 10;
    for (let i = start; i < start + 5; i++) {
        inputs[i].classList.remove("hidden");
    }

    const hideBtn = document.body.querySelector(".collapse-form-btn");
    hideBtn.classList.remove("hidden");
}

function collapseWordsSubmitForm() {

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

function updateNetworkInfoPanel(wordGraphData) {
    const nodeCount = networkInfoPanel.querySelector(".node-count");
    const edgeCount = networkInfoPanel.querySelector(".edge-count");

    nodeCount.textContent = `Nodes: ${wordGraphData.nodeCount}`;
    edgeCount.textContent = `Edges: ${wordGraphData.edgeCount}`;

    const nodeInfo = networkInfoPanel.querySelector(".complete-node-info > ul");
    while (nodeInfo.hasChildNodes()) {
        nodeInfo.removeChild(nodeInfo.lastChild);
    }
    if (wordGraphData.nodeCount === 0) {
        const li = document.createElement("li");
        li.textContent = "No nodes in network.";
        nodeInfo.appendChild(li);
    } else {
        for (const count of wordGraphData.edgeCounts) {
            const li = createNodeLinkText(count.word);
            if (wordGraphData.words.includes(count.word)) {
                li.style.color = "orange";
            }
            li.textContent = `${count.word}: ${count.edgeCount}`;
            nodeInfo.appendChild(li);
        }
    }
}

function updateAddedWords(wordGraphData) {
    addedWords.clear();
    wordGraphData.words.forEach(e => addedWords.add(e));
}

function displaySynonyms(word, synonymList) {
    const nodeInfoTitle = document.body.querySelector(".complete-node-info-title");
    const nodeInfo = document.body.querySelector(".complete-node-info");
    nodeInfoTitle.classList.add("hidden-temp");
    nodeInfo.classList.add("hidden-temp");

    const box = document.createElement("div");
    box.className = "more-node-info-container";

    const wordDiv = document.createElement("div");
    wordDiv.style.fontWeight = "bold";
    wordDiv.textContent = `${word}`;
    wordDiv.className = "selected-node";
    box.appendChild(wordDiv);

    box.appendChild(document.createElement("br"));

    const neighborCountDiv = document.createElement("div");
    const neighbors = network.getConnectedEdges(word);
    neighborCountDiv.textContent = `Neighbors: ${neighbors.length}`;
    box.appendChild(neighborCountDiv);

    if (neighbors.length > 0) {
        const neighborsScrollDiv = document.createElement("div");
        neighborsScrollDiv.className = "neighbor-info-container";
        box.appendChild(neighborsScrollDiv);

        const neighborList = document.createElement("ul");
        neighborsScrollDiv.appendChild(neighborList);

        box.appendChild(document.createElement("br"));

        const connected = network.getConnectedNodes(word);
        connected.sort((w1, w2) => {
            if ((addedWords.has(w1) && addedWords.has(w2))
                    || (!addedWords.has(w1) && !addedWords.has(w2))) {
                return w1.localeCompare(w2);
            } else if (addedWords.has(w1)) {
                return -1;
            } else {
                return 1;
            }
        });

        console.log(connected);

        for (const neighbor of connected) {
            const btn = createNodeLinkText(neighbor);
            if (addedWords.has(neighbor)) {
                btn.style.color = "orange";
            }
            neighborList.appendChild(btn);
        }
    }

    const titleDiv = document.createElement("div");
    box.appendChild(titleDiv);
    titleDiv.textContent = `Synonyms: ${synonymList.synonyms.length}`;

    if (synonymList.synonyms.length > 0) {
        const scrollDiv = document.createElement("div");
        scrollDiv.className = "word-info-container";
        box.appendChild(scrollDiv);

        const list = document.createElement("ul");
        scrollDiv.appendChild(list);

        for (const synonym of synonymList.synonyms) {
            const li = createNodeLinkText(synonym);

            if (network.findNode(synonym).length === 0) {
                li.style.color = "grey";
            } else if (addedWords.has(synonym)) {
                li.style.color = "orange";
            }

            if (network.getConnectedNodes(word).includes(synonym)) {
                li.style.fontWeight = 900;
            }
            list.appendChild(li);
        }
    }
    networkInfoPanel.appendChild(box);
}

function displayEmptySynonyms(word) {
    const box = document.createElement("div");
    box.className = "synonyms-container-empty";
    const span = document.createElement("span");
    span.textContent = `No synonyms were found for ${word}`;
    box.appendChild(span);
    leftSideContainer.appendChild(box);
}

function removeSynonymsDisplay() {
    const synonymDisplay = document.body.querySelector(".more-node-info-container");
    if (synonymDisplay !== null) {
        synonymDisplay.parentNode.removeChild(synonymDisplay);
    }

    const nodeInfoTitle = document.body.querySelector(".complete-node-info-title");
    const nodeInfo = document.body.querySelector(".complete-node-info");
    nodeInfoTitle.classList.remove("hidden-temp");
    nodeInfo.classList.remove("hidden-temp");
}

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

    network.on('click', function(properties) {
        const nodeId = network.getNodeAt({x:properties.event.srcEvent.offsetX, y:properties.event.srcEvent.offsetY});
        if (nodeId === undefined) {
            return;
        }
        network.selectNodes([nodeId], true);
    });

    network.on("selectNode", function(properties) {
        const nodeId = properties.nodes[0];
        showSynonyms(nodeId);
    });
    return network;
}

function clearAll() {
    const emptyData = {
        nodes: null,
        edges: null
    }
    network.setData(emptyData);
}

function createNodeLinkText(word) {
    const btn = document.createElement("button");
    btn.classList.add("text-button");
    btn.textContent = word;

    btn.addEventListener("click", event => {
        if (network.findNode(word).length > 0) {
            network.selectNodes([word], true);
        }
        removeSynonymsDisplay();
        showSynonyms(word);
    });
    return btn;
}

function showSynonyms(nodeId) {
    getSynonymList(nodeId)
        .then(response => {
            if (response.status === 404) {
                return Promise.reject("404 Not Found; no synonyms exist for given word");
            } else {
                return response.json();
            }
        })
        .then(synonymList => {
            displaySynonyms(nodeId, synonymList);
        })
        .catch(error => {
            console.log(error);
        });
}




        