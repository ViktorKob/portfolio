console.log("main.js loaded");

var renderer = require("./query_list_renderer.js");
var queryHistory = JSON.parse(localStorage.getItem("queryHistory"));
var queryHistoryRender = renderer.renderQueryList(queryHistory);

if (typeof document !== "undefined") {
  document.body.appendChild(queryHistoryRender);
} else {
   console.log(queryHistoryRender);
}