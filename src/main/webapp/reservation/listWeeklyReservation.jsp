<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="com.smashspot.reservation.model.ReservationTimeService" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Calendar" %>
<%
    ReservationTimeService reservationSvc = new ReservationTimeService();
    pageContext.setAttribute("reservationSvc", reservationSvc);
    
    // 設置今天日期
    pageContext.setAttribute("today", new Date());
    
    // 設置28天後的日期
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DAY_OF_MONTH, 28);
    pageContext.setAttribute("futureLimit", calendar.getTime());
%>
<%
    // 取得目前時間
    Calendar cal = Calendar.getInstance();
    
    // 取得 weekOffset 參數
    String weekOffsetStr = request.getParameter("weekOffset");
    int weekOffset = (weekOffsetStr != null) ? Integer.parseInt(weekOffsetStr) : 0;
    
    // 調整到本週日
    cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
    
    // 根據 weekOffset 調整週數
    cal.add(Calendar.DATE, weekOffset * 7);
    
    // 將計算後的日曆對象存入 pageContext
    pageContext.setAttribute("startDate", cal.getTime());
    
    // 建立一個新的 Calendar 對象來計算結束日期
    Calendar endCal = (Calendar) cal.clone();
    endCal.add(Calendar.DATE, 6);  // 加6天得到該週最後一天
    
    // 將結束日期存入 pageContext
    pageContext.setAttribute("endDate", endCal.getTime());
%>

<html>
<head>
    <title>場館週預約狀態</title>
    <style>
	body {
	    font-family: Arial, sans-serif;
	    margin: 20px;
	    background-color: #f5f5f5;
	}
	
	.calendar-header {
	    display: flex;
	    justify-content: space-between;
	    align-items: center;
	    margin-bottom: 20px;
	}
	
	.week-nav {
	    display: flex;
	    align-items: center;
	    gap: 10px;
	}
	
	.week-nav button {
	    padding: 5px 15px;
	    border: 1px solid #4169E1;
	    background-color: white;
	    color: #4169E1;
	    cursor: pointer;
	    border-radius: 4px;
	}
	
	table {
	    width: 100%;
	    border-collapse: collapse;
	    background-color: white;
	    box-shadow: 0 1px 3px rgba(0,0,0,0.2);
	    table-layout: fixed;
	}
	
	th, td {
	    border: 1px solid #ddd;
	    padding: 10px;
	    text-align: center;
	    width: 12.5%;
	    word-wrap: break-word;
	}
	
	th {
	    background-color: #4169E1;
	    color: white;
	}
	
	.time-slot {
	    background-color: #f2f2f2;
	    font-weight: bold;
	    width: 12.5%;
	}
	

		
    </style>
</head>
<body>
    <div class="calendar-header">
        <h2>${reservationList[0].stdmname}</h2>
        <div class="week-nav">
		    <button onclick="changeWeek(-1)"><</button>
		    <fmt:formatDate value="${startDate}" pattern="yyyy/MM/dd"/>
		    ~
		    <fmt:formatDate value="${endDate}" pattern="yyyy/MM/dd"/>
		    <button onclick="changeWeek(1)">></button>
        </div>
    </div>

    <table>
        <tr>
            <th>時段</th>
            <c:forEach var="i" begin="0" end="6">
                <th>
                    <c:choose>
                        <c:when test="${i == 0}">週日</c:when>
                        <c:when test="${i == 1}">週一</c:when>
                        <c:when test="${i == 2}">週二</c:when>
                        <c:when test="${i == 3}">週三</c:when>
                        <c:when test="${i == 4}">週四</c:when>
                        <c:when test="${i == 5}">週五</c:when>
                        <c:when test="${i == 6}">週六</c:when>
                    </c:choose>
                    <br>
		            <fmt:formatDate value="${startDate}" pattern="MM/dd"/>
		            <% cal.add(Calendar.DATE, 1); // 日期加1
		               pageContext.setAttribute("startDate", cal.getTime());
		            %>
                </th>
            </c:forEach>
        </tr>

        <!-- 時段列表 -->
		<!-- 時段列表 -->
		<c:forEach var="timeSlot" begin="4" end="10">
		    <tr>
		        <td class="time-slot">
		            <c:choose>
		                <c:when test="${timeSlot == 4}">08:00-10:00</c:when>
		                <c:when test="${timeSlot == 5}">10:00-12:00</c:when>
		                <c:when test="${timeSlot == 6}">12:00-14:00</c:when>
		                <c:when test="${timeSlot == 7}">14:00-16:00</c:when>
		                <c:when test="${timeSlot == 8}">16:00-18:00</c:when>
		                <c:when test="${timeSlot == 9}">18:00-20:00</c:when>
		                <c:when test="${timeSlot == 10}">20:00-22:00</c:when>
		            </c:choose>
		        </td>
		        
		        <%-- 明確遍歷七天 --%>
		        <c:forEach var="dayIndex" begin="0" end="6">
		            <td>
		                <c:set var="reservation" value="${reservationList[dayIndex]}" />
		                <c:choose>
		                	<%-- 預設顯示為尚未開放 --%>
		                	<c:when test="${empty reservation}">
		                        <span style="color: gray;">尚未開放</span>
		                    </c:when>
		                    <c:otherwise>
		                        <c:set var="availableCount" value="${reservationSvc.getAvailableCount(reservation.booked, reservation.rsvava, timeSlot)}"/>
		                        <c:choose>
		                            <c:when test="${reservation.dates.time lt today.time}">
		                                <span style="color: gray;">已過期</span>
		                            </c:when>
		                            <%-- 超過28天顯示為尚未開放 --%>
		                            <c:when test="${reservation.dates.time gt futureLimit.time}">
		                                <span style="color: gray;">尚未開放</span>
		                            </c:when>
		                            <c:when test='${reservation.rsvava.substring(timeSlot, timeSlot + 1) eq "x"}'>
		                                <span style="color: gray;">不開放</span>
		                            </c:when>
		                            <c:when test="${availableCount > 2}">
		                                <span style="color: green;">可預約: ${availableCount}</span>
		                            </c:when>
		                            <c:when test="${availableCount <= 2 && availableCount >= 1}">
		                                <span style="color: brown;">即將額滿: ${availableCount}</span>
		                            </c:when>
		                            <c:otherwise>
		                                <span style="color: red;">已額滿</span>
		                            </c:otherwise>
		                        </c:choose>
		                    </c:otherwise>
		                </c:choose>
		            </td>
		        </c:forEach>
		    </tr>
		</c:forEach>
    </table>

    <script>
        function changeWeek(offset) {
            const currentStdmid = '${param.stdmid}';
            const currentWeekOffset = ${empty weekOffset ? 0 : weekOffset};
            const newWeekOffset = currentWeekOffset + offset;
            
            // 限制 weekOffset 範圍在 0~4 之間
            if (newWeekOffset < 0) {
                newWeekOffset = 0;
                return; // 如果已經是第一週，就不執行後續操作
            }
            if (newWeekOffset > 4) {
                newWeekOffset = 4;
                return; // 如果已經是第四週，就不執行後續操作
            }
            
            const params = new URLSearchParams();
            params.append('action', 'getWeeklyReservation');
            params.append('stdmid', currentStdmid);
            params.append('weekOffset', newWeekOffset);
            
            window.location.href = '${pageContext.request.contextPath}/reservation/weekly?' + params.toString();
        }
    </script>
</body>
</html>