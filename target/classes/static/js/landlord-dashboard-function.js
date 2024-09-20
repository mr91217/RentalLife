document.addEventListener('click', function(event) {
    if (event.target && event.target.id === 'propertydeleteButton') {
        confirmDeletion(event);
    }
});

function confirmDeletion(event) {
    console.log("confirmDeletion function called");
    // event.preventDefault(); // 防止表單立即提交
    if (confirm("Are you sure you want to delete this item?")) {
        console.log("Confirmed deletion");
        document.getElementById("propertydeleteForm").submit();
    } else {
        // 如果點擊取消，不做任何操作
        event.preventDefault();
        console.log("cancel clicked");
    }
}