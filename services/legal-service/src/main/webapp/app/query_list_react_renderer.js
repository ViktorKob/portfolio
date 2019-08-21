const React = require("react");
const ReactDOM = require("react-dom");
const client = require("./client");
const SockJS = require('sockjs-client');
require('stompjs');

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

class HistoryItem extends React.Component{
	render() {
		var timeOfLogging = convertTimestamp(this.props.item.timeOfLogging);
		return (
			<tr>
				<td>{this.props.item.type}</td>
				<td>{timeOfLogging}</td>
			</tr>
		)
	}
}
  
class History extends React.Component{
	render() {
		const history = this.props.history.map(item =>
			<HistoryItem key={JSON.stringify(item)} item={item}/>
		);
		return (
			<table>
				<tbody>
					<tr>
						<th>Type</th>
						<th>Date of execution</th>
					</tr>
					{history}
				</tbody>
			</table>
		)
	}
}
  
function register(registrations) {
	const socket = SockJS('v1/selectors/web-socket');
	const stompClient = Stomp.over(socket);
	stompClient.connect({}, function(frame) {
		registrations.forEach(function (registration) {
			stompClient.subscribe(registration.route, registration.callback);
		});
	});
}

class QueryListRenderer extends React.Component {
	constructor(props){
		super(props);
		this.state = {history: []};
		this.setState = this.setState.bind(this);
		this.fetchData = this.fetchData.bind(this);		
	}
	
	componentDidMount() {
		register([
			{route: "/topic/legal-events/history", callback: this.fetchData}
		]);
		this.fetchData();
	}

	fetchData(){
		client({method: "GET", path: "v1/selectors/history"}).done(response => {
			this.setState({history: response.entity});
		});
	}
	
	render(){
		return (
			<History history = {this.state.history} />
		)
	}
}  

ReactDOM.render(
	<QueryListRenderer />,
	document.getElementById("reactiveQueryHistory")
)