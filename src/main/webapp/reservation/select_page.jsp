<%-- /webapp/reservation/select_page.jsp --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html>
<head>
    <title>場館預約查詢</title>
</head>
<body>
    <h2>場館預約查詢</h2>
    
    <form method="get" action="<%=request.getContextPath()%>/reservation/weekly">
        <input type="hidden" name="action" value="getWeeklyReservation">
        場館編號: <input type="text" name="stdmid">
        <input type="submit" value="查詢">
    </form>
</body>
</html>