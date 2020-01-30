//import
import axios from 'axios'
var config = require('../../config')

var frontendUrl = 'http://' + config.build.host + ':' + config.build.port
var backendUrl = 'https://' + config.build.backendHost + ':' + config.build.backendPort
var externalUrl = 'https://ecse321-group17.herokuapp.com'
var AXIOS = axios.create({
  baseURL: backendUrl,
  headers: { 'Access-Control-Allow-Origin': frontendUrl }
})

var EXTERNAL = axios.create({
  baseURL: externalUrl,
  headers: { 'Access-Control-Allow-Origin': frontendUrl }
})

//constructor methods
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
  name: "cooperator",
  data() {
    return {
      people: [],
      students: [],
      internships: [],
      newFirst: '',
      newLast: '',
      newEmail: '',
      errorInternship: '',
      employers: [],
      sID: '',
      response: [],
      show: true,
      form: {
        employer: '',
        internid: '',
        posname: '',
        year: '',
      },
      selected: '',
      options: [
          { text: 'Fall', value: 'FALL' },
          { text: 'Winter', value: 'WINTER' },
          { text: 'Summer', value: 'SUMMER' }
        ],
      selectedEmployer:'',
      employerOptions: [],
      employerNameOptions:[]
    };
  },
  // on create function gets all the employers and lists them in a list
  created: function() {

    //Gets the list of all the employers for the dropdown menu
    EXTERNAL.get(`/employers/`, {}, {})
    .then(response => {
      // JSON responses are automatically parsed.
      this.employers.push(response.data)
      var i;
      for(i = 0; i < response.data.length; i++) {
        this.employerOptions.push({text: this.employers[0][i].name + ' from  ' + this.employers[0][i].company , value: [this.employers[0][i].name, this.employers[0][i].company]});
//        {text: response[i].company, value: response[i].company }
      }

    })
    .catch(e => {
      var errorMsg =  e.response.data.message
      console.log(errorMsg)
      this.errorInternships= errorMsg
    });
  },
  methods: {
    /*Registers student to an internship
    *@param scheduledInternship, Position Name ,Employer name, Internship term, Internship year
    */
    registerInternship: function (scheduledInternship, name, employer, term, year) {
      var SID = this.$route.params.id;
      AXIOS.post(`/`+SID+`/register-internship/?studentId=`+SID+`&scheduledInternship=`+scheduledInternship+`&name=`+name+`&employer=`+employer+`&term=`+term+`&year=`+year, {}, {})
      .then(response => {
        // JSON responses are automatically parsed.
        this.internships.push(response.data)

        this.$nextTick(() => {
          this.$router.push({ path: `/user/`+ this.$route.params.id })
        });
      })
      .catch(e => {

        var errorMsg = e.response.data.message
        if(e.response.data.status === 400){
          errorMsg = 'Please fill out all the required fields'
        }
        console.log(errorMsg)
        this.errorInternship = errorMsg
      });
    }

  }
};
