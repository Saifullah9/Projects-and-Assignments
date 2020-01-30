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
class DocumentDto {
  constructor(url, name, documentId) {
		this.url = url;
		this.name = name;
		this.documentId = documentId;
  }
}
// intialization of variables and methods
export default {
  name: "cooperator",
  data() {
    return {
      form: {
        url: '',
        docname: '',
      },
      newDocuments: [],
      internships: [],
      newDocumentName: '',
      students: '',
      newDocumentURL: '',
      errorInternship: '',
      errorDocument: '',
      sID: '',
      response: []
    };
  },
  // on create method gets the specfic internships of the student.
  created: function() {
    var internshipId = this.$route.params.internshipId;
    var SID = this.$route.params.id;
    AXIOS.get(`/getStudent/` + SID)
    .then(response => {
      // JSON responses are automatically parsed.
      this.students = response.data
          this.errorOffers= ''
    })
    .catch(e => {
      var errorMsg =  e.response.data.message
      console.log(errorMsg)
      this.errorPerson = errorMsg;
    });
    //Gets specificInternship by internship ID
    AXIOS.get(`/specificInternship/?SpecificInternship=`+ internshipId, {}, {})
    .then(response => {
      // JSON responses are automatically parsed.
      this.internships.push(response.data)
    })
    .catch(e => {
      var errorMsg = e.response.data.message
      console.log(errorMsg)
      this.errorInternship= errorMsg
    });
  },
  methods: {
    /*Submits Document of Internship Evaluation (POST Request)
    @param document URL, document Name
    */
    submitInternshipEvaluation: function (documentURL, documentName) {
      var SID = this.$route.params.id
      AXIOS.post(`/`+SID+`/specific-internship/upload-doc/?documentURL=`+documentURL+`&documentName=`+documentName, {}, {})
      .then(response => {
      // JSON responses are automatically parsed.
      this.$nextTick(() => {
        this.newDocuments.push(response.data)
        newDocumentName: ''
        newDocumentURL: ''
        errorDocument: ''
        this.$router.go(0)
      });
    })
    .catch(e => {
      var errorMsg = e.response.data.message
      console.log(errorMsg)
      this.errorDocument= errorMsg
    })
    .finally(() => {
      // sending an email from the google api
      var Email =
      { send: function (a) {
        return new Promise(function (n, e)
        { a.nocache = Math.floor(1e6 * Math.random() + 1),
          a.Action = "Send"; var t = JSON.stringify(a);
           Email.ajaxPost("https://smtpjs.com/v3/smtpjs.aspx?", t,
        function (e) { n(e) }) }) },
         ajaxPost: function (e, n, t) {
        var a = Email.createCORSRequest("POST", e);
       a.setRequestHeader("Content-type", "application/x-www-form-urlencoded", 'Access-Control-Allow-Origin'),
          a.onload = function () {
             var e = a.responseText; null != t && t(e) },
          a.send(n) },
           ajax: function (e, n) {
            var t = Email.createCORSRequest("GET", e);
             t.onload = function () {
               var e = t.responseText; null != n && n(e) },
                t.send() },
         createCORSRequest: function (e, n) {
      var t = new XMLHttpRequest;
      return "withCredentials" in t ? t.open(e, n, !0) : "undefined" != typeof XDomainRequest ? (t = new XDomainRequest).open(e, n) : t = null, t } };
      Email.send({
          Host : "smtp.elasticemail.com",
          Username : "saifnodi@gmail.com",
          Password : "db793403-d952-4b1c-92f8-a6d6575a8d81",
          To : this.students.person.email,
          From : "saifnodi@gmail.com",
          Subject : "Cooperator Document Submission Confirmation",
          Body : 'This email is to confirm that your submission of ' +documentName+ '(URL: '+ documentURL + ') to your offer was successful.'
      }).then(
        message => alert(message)
      );
    });
  },
      // finish internship function finishes the internship
      finishInternship: function() {
        var SID = this.$route.params.id;
        AXIOS.put(`/` + SID + `/finishInternship/`)
        .then(response => {
          // JSON responses are automatically parsed.
          this.$nextTick(() => {
            this.errorInternship = ''
          });

        })
        .catch(e => {
          var errorMsg = e.response.data.message
          console.log(errorMsg)
          this.errorInternship = errorMsg
        });
    },

    // delete document from the internship using the url
    deleteDocumentInternship: function (docUrl){

      var SID = this.$route.params.id
      // http delete request
      AXIOS.delete(`/deleteDocumentInternship/`+SID+`/?docUrl=` + docUrl, {}, {})
      .then(response => {
      // JSON responses are automatically parsed.
      this.$nextTick(() => {

        this.errorOffers= ''
        this.$router.go(0)
      });
    })
    .catch(e => {
      var errorMsg = e.response.data.message
      console.log(errorMsg)
      this.errorOffers= errorMsg
    });
  }
}

}
