//根据传递过来的参数name获取对应的值
function getParameter(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)","i");
    var r = location.search.substr(1).match(reg);
    if (r!=null) return (r[2]); return null;
}

/*
//根据传递过来的参数name获取对应的值
function getParameter(text, name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)","i");
    var r = text.substr(1).match(reg);
    if (r!=null) return (r[2]); return null;
}*/
