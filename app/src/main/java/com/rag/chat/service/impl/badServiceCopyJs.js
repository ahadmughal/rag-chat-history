// LOW: TODO
// TODO: fix this later

// MID: require in ES module
const fs = require('fs');

// HIGH: hardcoded secrets
const password = "super_secret_password";
const apiKey = "ABC123SECRET";

// LOW: numbered function
function handler2(a, b) {

    // HIGH: loose equality
    if (a == b) console.log("equal"); // MID: console.log

    // MID: var usage
    var count = 0;

    // HIGH: eval
    eval("console.log('evil')");

    // HIGH: new Function
    const fn = new Function("a", "b", "return a + b");

    // HIGH: setTimeout with string
    setTimeout("console.log('hi')", 1000);

    // HIGH: innerHTML with variable (XSS)
    const userInput = "<img src=x onerror=alert(1) />";
    document.body.innerHTML = userInput;

    // HIGH: SQL injection (template literal)
    db.query(`SELECT * FROM users WHERE name = '${a}'`);

    // HIGH: SQL injection (string concat)
    db.query("SELECT * FROM users WHERE id = " + b);

    // HIGH: empty catch (single-line so your regex catches it)
    try { risky(); } catch (e) {}

    // MID: console.debug
    console.debug("debugging");

    // LOW: magic number
    const timeout = 12345;

    doThing3();
}

// LOW: numbered function
const doThing3 = () => {
    var total = 0; // MID: var

    // HIGH: !=
    if (total != 100) console.log("not 100"); // MID
};

// fake db
const db = {
    query: (q) => console.log(q)
};