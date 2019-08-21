var bootstrapRenderer = require("./query_list_renderer");
var reactRenderer = require("./query_list_react_renderer");

var queryHistory = JSON.parse(localStorage.getItem("queryHistory"));
var queryHistoryRender = bootstrapRenderer.renderQueryList(queryHistory);

if (typeof document !== "undefined") {
  document.body.appendChild(queryHistoryRender);
} else {
   console.log(queryHistoryRender); 
}