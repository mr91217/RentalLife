//var buttonEl = document.querySelector("#save-task");
var formEl = document.querySelector("#task-form");
var tasksToDoEl = document.querySelector("#tasks-to-do");
var tasksInProgressEl = document.querySelector("#tasks-in-progress");
var tasksCompletedEl = document.querySelector("#tasks-completed");
var pageContentEl = document.querySelector("#page-content");
var taskIdCounter = 0;

// create array to hold tasks for saving
var tasks = [];
/*
buttonEl.addEventListener("click", function(){
    var listItemEl = document.createElement("li");
    listItemEl.className = "task-item";
    listItemEl.textContent = "NEW TASK";
    tasksToDoEl.appendChild(listItemEl);
});
*/
var taskFormHandler = function(event){

    event.preventDefault();
    //var taskNameInput = document.querySelector("input[name='task-name']");
    var taskNameInput = document.querySelector("input[name='task-name']").value;
    var taskTypeInput = document.querySelector("select[name='task-type']").value;

    // check if input values are empty strings
    if(!taskNameInput || !taskTypeInput){
        alert("You need to fill out the task form!");
        return false;

    }
    formEl.reset();

    var isEdit = formEl.hasAttribute("data-task-id");
    // has data attribute, so get task id and call function to complete edit process
    if (isEdit) {
        var taskId = formEl.getAttribute("data-task-id");
        completeEditTask(taskNameInput, taskTypeInput, taskId);

    }else{
        // no data attribute, so create object as normal and pass to createTaskEl function
        // package up data as an object
        var taskDataObj = {
            name: taskNameInput,
            type: taskTypeInput,
            status:"to do",
        };
        // send it as an argument to createTaskEl
        createTaskEl(taskDataObj);
    }


    /*// create list item
    var listItemEl = document.createElement("li");
    listItemEl.className = "task-item";

    // create div to hold task info and add to list item
    var taskInfoEl = document.createElement("div");
    // give it a class name
    taskInfoEl.className = "task-info";
    // add HTML content to div
    taskInfoEl.innerHTML = "<h3 class='task-name'>" + taskNameInput + "</h3><span class='task-type'>" + taskTypeInput + "</span>";
    listItemEl.appendChild(taskInfoEl);
    //listItemEl.textContent = taskNameInput;

    // add entire list item to list
    tasksToDoEl.appendChild(listItemEl);       */

};



var createTaskEl = function(taskDataObj){
    // create list item
    var listItemEl = document.createElement("li");
    listItemEl.className = "task-item";

    // add task id as a custom attribute
    listItemEl.setAttribute("data-task-id",taskIdCounter);

    // create div to hold task info and add to list item
    var taskInfoEl = document.createElement("div");
    taskInfoEl.className = "task-info";
    taskInfoEl.innerHTML = "<h3 class='task-name'>" + taskDataObj.name + "</h3><span class='task-type'>" + taskDataObj.type + "</span>";

    listItemEl.appendChild(taskInfoEl);

    var taskActionsEl = createTaskActions(taskIdCounter);
    listItemEl.appendChild(taskActionsEl);

    // add entire list item to list
    tasksToDoEl.appendChild(listItemEl);

    switch (taskDataObj.status) {
        case "to do":
            taskActionsEl.querySelector("select[name='status-change']").selectedIndex = 0;
            tasksToDoEl.append(listItemEl);
            break;
        case "in progress":
            taskActionsEl.querySelector("select[name='status-change']").selectedIndex = 1;
            tasksInProgressEl.append(listItemEl);
            break;
        case "completed":
            taskActionsEl.querySelector("select[name='status-change']").selectedIndex = 2;
            tasksCompletedEl.append(listItemEl);
            break;
        default:
            console.log("Something went wrong!");
    }


    // save task as an object with name, type, status, and id properties then push it into tasks array
    taskDataObj.id = taskIdCounter;

    tasks.push(taskDataObj);

    // save tasks to localStorage
    saveTasks();

    // increase task counter for next unique id
    taskIdCounter++;

    console.log(taskDataObj);
    console.log(taskDataObj.status);


};

var createTaskActions = function(taskId){
    var actionContainerEl = document.createElement("div");
    actionContainerEl.className = "task-actions";

    // create edit button
    var editButtonEl = document.createElement("button");
    editButtonEl.textContent = "Edit";
    editButtonEl.className = "btn edit-btn";
    editButtonEl.setAttribute("data-task-id", taskId);

    actionContainerEl.appendChild(editButtonEl);

    // create delete button
    var deleteButtonEl = document.createElement("button");
    deleteButtonEl.textContent = "Delete";
    deleteButtonEl.className = "btn delete-btn";
    deleteButtonEl.setAttribute("data-task-id", taskId);

    actionContainerEl.appendChild(deleteButtonEl);

    // create select options
    var statusSelectEl = document.createElement("select");

    statusSelectEl.setAttribute("name", "status-change");
    statusSelectEl.setAttribute("data-task-id", taskId);
    statusSelectEl.className = "select-status";

    actionContainerEl.appendChild(statusSelectEl);

    var statusChoices = ["To Do", "In Progress", "Completed"];

    for (var i = 0; i < statusChoices.length; i++) {
        // create option element
        var statusOptionEl = document.createElement("option");
        statusOptionEl.setAttribute("value", statusChoices[i]);
        statusOptionEl.textContent = statusChoices[i];
        // append to select
        statusSelectEl.appendChild(statusOptionEl);
    }

    return actionContainerEl;



};

var completeEditTask = function(taskName, taskType, taskId) {
    // find the matching task list item
    var taskSelected = document.querySelector(".task-item[data-task-id='" + taskId + "']");

    // set new values
    taskSelected.querySelector("h3.task-name").textContent = taskName;
    taskSelected.querySelector("span.task-type").textContent = taskType;

    // loop through tasks array and task object with new content
    for (var i = 0; i < tasks.length; i++) {
        if (tasks[i].id === parseInt(taskId)) {
            tasks[i].name = taskName;
            tasks[i].type = taskType;
        }

    };



    alert("Task Updated");
    formEl.removeAttribute("data-task-id");
    formEl.querySelector("#save-task").textContent = "Add Task";
    saveTasks();
};

var taskButtonHandler = function(event){
    // get target element from event
    var targetEl = event.target;

    // edit button was clicked

    if (targetEl.matches(".edit-btn")) {
        // get the element's task id
        var taskId = targetEl.getAttribute("data-task-id");
        editTask(taskId);
    }else if (targetEl.matches(".delete-btn")) {
        var taskId = targetEl.getAttribute("data-task-id");
        deleteTask(taskId);
        // delete button was clicked
    }
};

var taskStatusChangeHandler = function(event){
    console.log(event.target.value);
    // get the task item's id
    var taskId = event.target.getAttribute("data-task-id");

    // find the parent task item element based on the id
    var taskSelected = document.querySelector(".task-item[data-task-id='" + taskId + "']");

    // get the currently selected option's value and convert to lowercase
    var statusValue = event.target.value.toLowerCase();

    if (statusValue === "to do") {
        tasksToDoEl.appendChild(taskSelected);
    } else if (statusValue === "in progress") {
        tasksInProgressEl.appendChild(taskSelected);
    } else if (statusValue === "completed") {
        tasksCompletedEl.appendChild(taskSelected);
    }

    // update task's in tasks array
    for (var i = 0; i < tasks.length; i++) {
        if(tasks[i].id === parseInt(taskId)){
            tasks[i].status = statusValue;
        }

    }

    saveTasks();

};

var editTask = function (taskId){
    console.log("editing task #" + taskId);
    // get task list item element
    var taskSelected = document.querySelector(".task-item[data-task-id='" + taskId + "']");
    // get content from task name and type
    var taskName = taskSelected.querySelector("h3.task-name").textContent;
    console.log(taskName);
    var taskType = taskSelected.querySelector("span.task-type").textContent;
    console.log(taskType);

    // write values of taskname and taskType to form to be edited
    document.querySelector("input[name='task-name']").value = taskName;
    document.querySelector("select[name='task-type']").value = taskType;

    // set data attribute to the form with a value of the task's id so it knows which one is being edited
    formEl.setAttribute("data-task-id", taskId);
    // update form's button to reflect editing a task rather than creating a new one
    formEl.querySelector("#save-task").textContent = "Save Task";


};



var deleteTask = function(taskId) {
    var taskSelected = document.querySelector(".task-item[data-task-id='" + taskId + "']");
    taskSelected.remove();
    console.log(taskSelected);
    // create new array to hold updated list of tasks
    var updatedTaskArr = [];
    // loop through current tasks
    for (var i = 0; i < tasks.length; i++) {
        // if tasks[i].id doesn't match the value of taskId, let's keep that task and push it into the new array
        if (tasks[i].id !== parseInt(taskId)) {
            updatedTaskArr.push(tasks[i]);
        }
    }
    // reassign tasks array to be the same as updatedTaskArr
    tasks = updatedTaskArr;
    saveTasks();
};

var saveTasks = function(){
    // localStorage.setItem("TASKs", tasks);
    localStorage.setItem("tasks", JSON.stringify(tasks));

};

var loadTasks = function (){
    // var tasks = localStorage.getItem("TASKs", tasks);
    // tasks = localStorage.getItem("tasks");
    var savedTasks = localStorage.getItem("tasks");
    // console.log(tasks);
    // if (!tasks) {
    //     tasks = [];
    //     return false;
    // }
    if (!savedTasks) {
        // tasks = [];
        return false;
    }
    savedTasks = JSON.parse(savedTasks);
    // tasks = JSON.parse(tasks);
    // console.log(tasks);
    // return false;

    // loop through savedTasks array
    for (var i = 0; i < savedTasks.length; i++) {
        // pass each task object into the `createTaskEl()` function
        createTaskEl(savedTasks[i]);

    }
    console.log(savedTasks)

    // for (var i = 0; i < tasks.length; i++) {
    //     console.log(tasks[i]);
    //     tasks[i].id = taskIdCounter;
    //     // console.log(tasks[i]);
    //     var listItemEl = document.createElement("li");
    //     listItemEl.className = "task-item";
    //     listItemEl.setAttribute("data-task-id", tasks[i].id);
    //     // console.log(listItemEl);
    //     var taskInfoEl = document.createElement("div");
    //     taskInfoEl.className = "task-info";
    //     taskInfoEl.innerHTML = "<h3 class='task-name'>" + tasks[i].name + "</h3><span class='task-type'>" + tasks[i].type + "</span>";
    //     listItemEl.appendChild(taskInfoEl);
    //     var taskActionsEl = createTaskActions(tasks[i].id);
    //     listItemEl.appendChild(taskActionsEl);
    //     // console.log(listItemEl);
    //     if (tasks[i].status === "to do") {
    //         listItemEl.querySelector("select[name='status-change']").selectedIndex = 0;
    //         tasksToDoEl.appendChild(listItemEl);
    //     }else if (tasks[i].status === "in progress") {
    //         listItemEl.querySelector("select[name='status-change']").selectedIndex = 1;
    //         tasksInProgressEl.appendChild(listItemEl);
    //     }else if (tasks[i].status === "completed") {
    //         listItemEl.querySelector("select[name='status-change']").selectedIndex = 2;
    //         tasksCompletedEl.appendChild(listItemEl);
    //     }
    //     taskIdCounter++ ;
    //     console.log(listItemEl)
    // }

    // return false;

};

//buttonEl.addEventListener("click", taskFormHandler);
// Create a new task
formEl.addEventListener("submit", taskFormHandler);
// for edit and delete buttons
pageContentEl.addEventListener("click", taskButtonHandler);
// for changing the status
pageContentEl.addEventListener("change", taskStatusChangeHandler);

loadTasks();