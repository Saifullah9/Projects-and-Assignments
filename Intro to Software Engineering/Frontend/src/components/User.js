// imports
import axios from 'axios'
var config = require('../../config')

var frontendUrl = 'http://' + config.build.host + ':' + config.build.port
var backendUrl = 'https://' + config.build.backendHost + ':' + config.build.backendPort

var AXIOS = axios.create({
  baseURL: backendUrl,
  headers: { 'Access-Control-Allow-Origin': frontendUrl }
})

//constructor methods
class PersonDto {
  constructor(first, last, email) {
    this.first = first;
    this.last = last;
    this.email = email;
  }
}

class StudentDto {
  constructor(person, studentID) {
    this.studentID = studentID;
    this.person = person;
    this.termsRemaining = 4;
    this.termsFinished = 0;
  }
}
class scheduledInternshipDto {
  constructor(name, positionId, employer, id, term){
		this.name = name;
		this.positionId = positionId;
		this.employer = employer;
		this.id = id;
		this.term = term;
  }
}
class specificInternshipDto {
  constructor(scheduledInternshipId, studentId, year, internshipId) {
		this.scheduledInternshipId = scheduledInternshipId;
		this.studentId = studentId;
		this.year = year;
		this.isCompleted = false;
		this.internshipId = internshipId;
  }
}

// initialization of the variables and methods
export default {
  name: "user",
  data() {
    return {
      people: [],
      students: '',
      specificInternships: [],
      scheduledInternships: [],
      newFirst: '',
      newLast: '',
      erroroffers: '',
      header: '',
      message: '',
      message1: '',
      newEmail: '',
      errorPerson: '',
      errorInternships:'',
      errorScheduled: '',
      sID: '',
      alloffers: [],
      response: []
    };
  },
// on create method gets all internships for the student id
  created: function() {
    var SID = this.$route.params.id;

    //Gets list of all internships of Student by student ID (GET Request)
    AXIOS.get(`/getAllInternships/?Student=`+SID, {}, {})
    .then(response => {
      // JSON responses are automatically parsed.
      this.specificInternships.push(response.data)
    })
    .catch(e => {
      var errorMsg =  e.response.data.message
      console.log(errorMsg)
      this.errorInternships= errorMsg
    });
    var i;
    for(i = 0; i < this.specificInternships.length; i++){
      getScheduledInternship(this.specificInternships[i].internshipId);
    }
    AXIOS.get(`/getOffers/`+ SID, {}, {})
    .then(response => {
      // JSON responses are automatically parsed.
      this.alloffers.push(response.data)
      this.errorOffers= ''

    })
    .catch(e => {
      var errorMsg = e.response.data.message
      console.log(errorMsg)
      this.errorOffers1= errorMsg
    });

    AXIOS.get(`/getStudent/` + SID)
    .then(response => {
      // JSON responses are automatically parsed.
      this.$nextTick(() => {
        this.students = response.data
        this.errorPerson = ''
        // ALERT notification
        //-------------------------------------------------------------------------
        if(this.students.specificInternships.length < 4){ //still have more internships to go


        if(this.students.offers.length != 0){ //active offer
          if(this.students.offers[0].documents.length < 2){
            this.message = 'You still need to submit some documents to accompany your offer for internship. Submit them in the Offer Management tab!';
          } else {
            this.message = 'Your offer for internship is awaiting validation from an administrator. Sit tight!';
          }
        } else { //no active offer
          this.message = 'You don\'t have an active offer of internship yet. Create the offer above!';
        }




        if(this.students.specificInternships.length === 0 || this.students.specificInternships[this.students.specificInternships.length-1].completed) {
          if(this.alloffers[0].length === 0 || !(this.alloffers[0][this.alloffers.length - 1].validated)){
            this.message1 = 'Nothing.';
          }
          else {
            this.message1 = 'Your most recent offer has been validated. You may now apply for an internship!';
            this.message = 'You may submit another offer of internship if you\'d like.';
          }
        } else {
          if(this.students.specificInternships[this.students.specificInternships.length-1].documents.length < 4){
            this.message1 = 'Your internship is underway! Select your internship and submit a document below.';
            if(this.message === 'You don\'t have an active offer of internship yet. Create the offer above!' || this.message === 'You may submit another offer of internship if you\'d like.'){
              this.message = 'You may submit another offer of internship if you\'d like.';
            }
          }
          else {
            this.message1 = 'Your internship documents are awaiting approval from the administrator, so that the internship may be officially completed. Sit tight!';
            if(this.message === 'You don\'t have an offer of internship yet. Create the offer above!' || this.message === 'You may submit another offer of internship if you\'d like.'){
              this.message = 'You may submit another offer of internship if you\'d like.';
            }
          }
        }
        if(this.message1 === ''){
          this.message1 = 'Nothing.';
        }
      }




      else { //#of internships is greater than or equal to 4
        this.message = 'You do not need to submit any more offers of internship.'
        if(this.students.specificInternships[this.students.specificInternships.length-1].completed === 'false'){
          if(this.students.specificInternships[this.students.specificInternships.length-1].documents.length < 4){
            this.message1 = 'Your internship is underway! Select your internship and submit a document below.';
          }
          else {
            this.message1 = 'Your internship documents are awaiting approval from the administrator, so that the internship may be officially completed. Sit tight!';
          }
        } else {
            this.message1 = 'Your internships are now completed. Happy Browsing!';
        }
      }




      });
    })
    .catch(e => {
      var errorMsg =  e.response.data.message
      console.log(errorMsg)
      this.errorPerson = errorMsg;
    });


  },
  methods: {

    /*Creates Person
    *@param first name, last name, email address
    *@return PersonDto
    */
    createPerson: function(name, last, emailAdress) {
      var p = new PersonDto(name, last, emailAdress);
      this.people.push(p);
      this.newPerson = '';
      return p;
    },

    /*Creates Student
    *@param Person, student ID
    *@return StudentDto
    */
    createStudent: function(person, sId) {
      //embbed createPerson function here?
      var st = new StudentDto(person, sId);
      this.students.push(st);
      this.st = '';
      return st;
    },

    /*Gets list scheduled internships
    *@param SpecificInternship ID
    *@return list of scheduled internships
    */
    getScheduledInternship: function (SIID) {
    AXIOS.get(`/getScheduledInternship/?SpecificInternship=`+SIID, {}, {})
    .then(response => {
      // JSON responses are automatically parsed.
      this.scheduledInternships.push(response.data)
    })
    .catch(e => {
      var errorMsg =  e.response.data.message
      console.log(errorMsg)
      this.errorScheduled= errorMsg
    });
  }
  }
};
