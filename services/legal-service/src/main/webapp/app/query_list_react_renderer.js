import Accordion from "react-bootstrap/Accordion";
import Card from "react-bootstrap/Card";
import CardGroup from 'react-bootstrap/CardGroup'
import Col from 'react-bootstrap/Col'
import Container from "react-bootstrap/Container";
import Row from 'react-bootstrap/Row'

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

function has(object, propertyName){
	return object.__proto__.hasOwnProperty(propertyName); 
}

class HistoryItem extends React.Component{
	render() {
		var justification = has(this.props.item.legalInfo, "li_justification")? "No justification was given" : this.props.item.legalInfo.li_justification;
		var before = !has(this.props.item.legalInfo, "li_upperBound")? convertTimestamp(this.props.item.legalInfo.li_upperBound) : "No latest date was given";
		var after = !has(this.props.item.legalInfo, "li_lowerBound")? convertTimestamp(this.props.item.legalInfo.li_lowerBound) : "No earliest date was given";
		return (
			<Card>
				<Card.Header>
					{this.props.item.type} - {convertTimestamp(this.props.item.timeOfLogging)}
				</Card.Header>
				<Card.Body>
					<CardGroup>
						<Card>
							<Card.Header>
								Selector
							</Card.Header>
							<Card.Body>
								<Container>
									<Row>
										<Col xs = {2}>
											Type: 
										</Col>
										<Col xs = {4}>
											{this.props.item.selectorId.dti_type}
										</Col>
									</Row>
									<Row>
										<Col xs = {2}>
											Uid: 
										</Col>
										<Col xs = {4}>
											{this.props.item.selectorId.dti_uid}
										</Col>
									</Row>
								</Container>
							</Card.Body>
						</Card>
						<Card>
							<Card.Header>
								Legal Information
							</Card.Header>
							<Card.Body>
								<Row>
									<Col xs = {4}>
										User: 
									</Col>
									<Col xs = {8}>
										{this.props.item.legalInfo.li_user}
									</Col>
								</Row>
								<Row>
									<Col xs = {4}>
										Justification: 
									</Col>
									<Col xs = {8}>
										{justification}
									</Col>
								</Row>
								<Row>
									<Col xs = {4}>
										Before: 
									</Col>
									<Col xs = {8}>
										{before}
									</Col>
								</Row>
								<Row>
									<Col xs = {4}>
										After: 
									</Col>
									<Col xs = {8}>
										{after}
									</Col>
								</Row>
							</Card.Body>
						</Card>
					</CardGroup>
				</Card.Body>
			</Card>
		)
	}
}
  
class History extends React.Component{
	render() {
		const history = this.props.history.map(item =>
			<HistoryItem key={JSON.stringify(item)} item={item}/>
		);
		return (
			<Card>
				<Card.Title>Queries accepted by the service</Card.Title>
				<Card.Body>
					<Accordion>
						{history}
					</Accordion>
				</Card.Body>
			</Card>
		)
	}
}
  
function register(registrations) {
	const socket = SockJS('stomp'); 
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
		client({method: "GET", path: "v1/selectors/history"}).then(response => {
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