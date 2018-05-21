// declare the event emitter var here to be useed everywhere in your app
import { EventEmitter } from 'fbemitter';

// mr Emitter is your friendly next door emitter
const EventBus = new EventEmitter();

export default EventBus;