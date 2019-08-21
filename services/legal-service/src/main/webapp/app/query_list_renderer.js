function br(){
	return document.createElement("br"); 
}

function textNode(type, text){
	if(type !== null){
		var node = document.createElement(type);
		node.appendChild(document.createTextNode(text));
		return node; 
	} else {
		return document.createTextNode(text);
	}	
}

function zeropad(number){
	var numberString = "" + number;
	while(numberString.length < 2){
		numberString = "0" + numberString;
	}
	return numberString;
}

// Based on 'https://stackoverflow.com/questions/847185/convert-a-unix-timestamp-to-time-in-javascript'
function convertTimestamp(timestamp){
  var a = new Date(timestamp);
  var months = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];
  var year = a.getFullYear();
  var month = months[a.getMonth()];
  var date = a.getDate();
  var hour = a.getHours();
  var min = a.getMinutes();
  var sec = a.getSeconds();
  var time = month + ', ' + date + ' ' + year + ' - ' + zeropad(hour) + ':' + zeropad(min) + ':' + zeropad(sec) ;
  return time;
}

function date(timestamp){
	return document.createTextNode(convertTimestamp(timestamp)); 
}

function div(type, items){
	var node = document.createElement("div");
	node.className = type;
	for (i = 0; i < items.length; i++){
		node.appendChild(items[i]);
	}
	return node; 
}

function li(item){
	var node = document.createElement("LI");
	node.className = "list-group-item";
	node.appendChild(item);
	return node;
} 

function ul(items){
	var node = document.createElement("ul");
	node.className = "list-group";
	for (i = 0; i < items.length; i++){
		node.appendChild(li(items[i]));
	}
	return node; 
}

function renderSelector(selector){
	return document.createTextNode("Selector: " + selector.dti_type + "-" + selector.dti_uid);
}

function renderLegalInformation(legalInfo){
	user = document.createTextNode("User: " + legalInfo.li_user);
	if (legalInfo.li_justification !== null){
		justification = textNode("b", "Justification: " + legalInfo.li_justification);
	} else {
		justification = textNode("i", "No justification given");
	}
	return div("container", [user, br(), justification]);
}

function renderItem(item){
	selector = renderSelector(item.selectorId);
	legalInfo = renderLegalInformation(item.legalInfo);

	var body = div("card", [ul([selector, div("card-body", [legalInfo])])]);

	var render = document.createElement("LI");
	render.className = "list-group-item";
	render.appendChild(div("container", [textNode("h5", "Type: " + item.type), date(item.timeOfLogging), body]));
	return render;
}
  
var renderedValues;

function renderQuery(value, index, array){
	renderedValues.appendChild(renderItem(value));
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
		return div("container", [textNode("H3", "Queries accepted by the service"), renderedValues]);
	}  
}