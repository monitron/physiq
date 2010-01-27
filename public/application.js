$(document).ready(function() {
  /* Open and close stats editor when links are clicked */
  $("#openEditStats").click(function(event) {
    $("#stats").addClass("editing");
  });
  $("#closeEditStats").click(function(event) {
    $("#stats").removeClass("editing");
  });

  /* Show and hide simple food editor */
  $("#openAddSimpleFood").click(function(event) {
    $("#addSimpleFood").removeClass("inactive");
    $("#openAddSimpleFood").hide();
  });
  $("#closeAddSimpleFood").click(function(event) {
    $("#addSimpleFood").addClass("inactive");
    $("#openAddSimpleFood").show();
  });
});