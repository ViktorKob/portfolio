var renderedValues;

function renderQuery(value, index, array){
	var item = document.createElement("LI");
	item.className = "list-group-item";
	item.appendChild(document.createTextNode(value.key));
	renderedValues.appendChild(item);
}
  
module.exports = {
	renderQueryList: function(queryHistory) {
		renderedValues = document.createElement("UL");
		renderedValues.className = "list-group";
		if (typeof queryHistory !== "undefined"){
			queryHistory.forEach(renderQuery);
		}else{
			var item = document.createElement("LI");
			item.className = "list-group-item";
			item.appendChild(document.createTextNode("No queries rendered"));
			renderedValues.appendChild(item);  	
		}
		return renderedValues;
	}  
}