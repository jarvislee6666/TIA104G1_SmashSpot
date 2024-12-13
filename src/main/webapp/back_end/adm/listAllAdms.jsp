<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
	rel="stylesheet">
<link
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css"
	rel="stylesheet">
<style>
:root {
	--primary: #2563eb;
	--primary-hover: #1d4ed8;
	--secondary: #64748b;
	--danger: #ef4444;
	--success: #22c55e;
	--background: #f8fafc;
	--surface: #ffffff;
	--text: #0f172a;
	--border: #e2e8f0;
}

body {
	background-color: var(--background);
	min-height: 100vh;
}

.navbar {
	padding: 0.75rem 1.5rem;
	background-color: var(--surface);
	box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.navbar .navbar-brand {
	font-size: 1.5rem;
	font-weight: 600;
	padding: 0;
	margin: 0;
	margin-right: auto;
	white-space: nowrap;
}

.navbar-content {
	display: flex;
	align-items: center;
	width: 100%;
	gap: 2rem;
}

.nav-buttons {
	display: flex;
	gap: 1rem;
}

.nav-buttons .nav-link {
	color: var(--text);
	padding: 0.5rem 1rem;
	border-radius: 0.375rem;
	transition: all 0.2s;
	white-space: nowrap;
	font-size: 1rem;
}

.nav-pills .nav-link {
	color: var(--text);
	transition: all 0.2s;
	padding: 1rem 1.5rem;
	font-size: 1rem;
	border-bottom: 1px solid var(--border);
	border-radius: 0;
}

.nav-pills .nav-link.active {
	background-color: var(--primary);
	color: white;
}

.card {
	border: 1px solid var(--border);
	box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.table th {
	background-color: var(--surface);
	font-weight: 600;
	color: var(--secondary);
	padding: 1rem;
	border-bottom: 2px solid var(--border);
}

.table td {
	padding: 1rem;
	vertical-align: middle;
}

.status-badge {
	padding: 0.25rem 0.75rem;
	border-radius: 0.25rem;
	font-size: 0.875rem;
	font-weight: 500;
}

.status-正常 {
	background-color: #ecfdf5;
	color: #047857;
}

.status-已停用 {
	background-color: #fef2f2;
	color: #991b1b;
}

.rank-badge {
	padding: 0.25rem 0.75rem;
	border-radius: 0.25rem;
	font-size: 0.875rem;
	background-color: #f3f4f6;
	color: #374151;
}

.btn-add {
	background-color: var(--primary);
	color: white;
}

.btn-add:hover {
	background-color: var(--primary-hover);
	color: white;
}

.modal-title {
	font-weight: 600;
}

.search-item {
	display: inline-block;
	margin-right: 20px;
}
</style>
</head>
<body>
	<nav class="navbar navbar-light mb-4">
		<div class="container-fluid">
			<div class="navbar-content">
				<a class="navbar-brand" href="#">管理員後台</a>
				<div class="nav-buttons">
					<a class="nav-link active" href="#"> <i
						class="fas fa-users me-2"></i>員工帳號管理
					</a> <a class="nav-link" href="#"> <i
						class="fas fa-ticket-alt me-2"></i>優惠券管理
					</a> <a class="nav-link" href="#"> <i class="fas fa-store me-2"></i>商城管理
					</a> <a class="nav-link" href="#"> <i
						class="fas fa-map-marker-alt me-2"></i>場地管理
					</a> <a class="nav-link" href="#"> <i class="fas fa-comments me-2"></i>論壇管理
					</a> <a class="nav-link" href="#"> <i class="fas fa-headset me-2"></i>客服中心
					</a> <a class="nav-link" href="#"> <i
						class="fas fa-user-circle me-2"></i>會員帳號管理
					</a>
				</div>
				<div class="d-flex align-items-center border-start ps-4">
					<div class="nav-item me-3">
						<span class="nav-link"> <i class="fas fa-user me-1"></i>管理員
						</span>
					</div>
					<button class="btn btn-outline-secondary btn-sm">
						<i class="fas fa-sign-out-alt me-1"></i>登出
					</button>
				</div>
			</div>
		</div>
	</nav>
	<div class="container-fluid">
		<div class="row">
			<!-- 左側選單 -->
			<div class="col-md-2">
				<div class="card">
					<div class="card-body p-0">
						<div class="nav flex-column nav-pills">
							<a class="nav-link"
								href="${pageContext.request.contextPath}/adm/adm.do?action=getAll">
								<i class="fas fa-list me-2"></i>管理者列表
							</a>
						</div>
					</div>
				</div>
			</div>
			<div class="col-md-10">
				<div class="card">
					<div class="card-body">
						<div
							class="d-flex justify-content-between align-items-center mb-4">
							<h5 class="card-title mb-0">管理員列表</h5>
							<form action="${pageContext.request.contextPath}/adm/adm.do"
								method="post">

								<div>
									<div class="search-item">
										<label>姓名：</label> <input type="text" name="admname"
											value="${param.admname}">
									</div>

									<div class="search-item">
										<label>帳號狀態：</label> <select name="admsta">
											<option value="">請選擇狀態</option>
											<option value="true"
												${param.admsta == 'true' ? 'selected' : ''}>正常</option>
											<option value="false"
												${param.admsta == 'false' ? 'selected' : ''}>停用</option>
										</select>
									</div>

									<div class="search-item">
										<label>權限等級：</label> <select name="supvsr">
											<option value="">請選擇等級</option>
											<option value="true"
												${param.supvsr == 'true' ? 'selected' : ''}>高級</option>
											<option value="false"
												${param.supvsr == 'false' ? 'selected' : ''}>一般</option>
										</select>
									</div>
									<input type="submit" value="查詢">
								</div>

								<input type="hidden" name="action" value="compositeQuery">
							</form>
							<button class="btn btn-add" data-bs-toggle="modal"
								data-bs-target="#addEmployeeModal">
								<i class="fas fa-plus me-2"></i>新增管理員帳號
							</button>
						</div>

						<div class="table-responsive">
							<table class="table">
								<thead>
									<tr>
										<th>編號</th>
										<th>管理員姓名</th>
										<th>Email</th>
										<th>密碼</th>
										<th>連絡電話</th>
										<th>生日</th>
										<th>雇用日期</th>
										<th>帳號狀態</th>
										<th>權限等級</th>
										<th>編輯</th>
									</tr>
								</thead>
								<c:forEach var="adm" items="${admList}">
									<tr>
										<td>${adm.admid}</td>
										<td>${adm.admname}</td>
										<td>${adm.admemail}</td>
										<td>${adm.admpassword}</td>
										<td>${adm.admphone}</td>
										<td>${adm.admbday}</td>
										<td>${adm.hrdate}</td>
										<td>${adm.admsta ? '正常' : '停用'}</td>
										<td>${adm.supvsr ? '高級' : '一般'}</td>
									</tr>
								</c:forEach>
							</table>
							<c:if test="${admPageQty > 0}">
								<b><font color=red>第${currentPage}/${admPageQty}頁</font></b>
							</c:if>
							<c:if test="${currentPage > 1}">
								<a
									href="${pageContext.request.contextPath}/adm/adm.do?action=getAll&page=1">至第一頁</a>&nbsp;
	</c:if>
							<c:if test="${currentPage - 1 != 0}">
								<a
									href="${pageContext.request.contextPath}/adm/adm.do?action=getAll&page=${currentPage - 1}">上一頁</a>&nbsp;
	</c:if>
							<c:if test="${currentPage + 1 <= admPageQty}">
								<a
									href="${pageContext.request.contextPath}/adm/adm.do?action=getAll&page=${currentPage + 1}">下一頁</a>&nbsp;
	</c:if>
							<c:if test="${currentPage != admPageQty}">
								<a
									href="${pageContext.request.contextPath}/adm/adm.do?action=getAll&page=${admPageQty}">至最後一頁</a>&nbsp;
	</c:if>
							<br> <br> <br>
							<div class="modal fade" id="addEmployeeModal" tabindex="-1">
								<div class="modal-dialog">
									<div class="modal-content">
										<div class="modal-header">
											<h5 class="modal-title">新增員工帳號</h5>
											<button type="button" class="btn-close"
												data-bs-dismiss="modal"></button>
										</div>
										<div class="modal-body">
											<form id="addEmployeeForm">
												<div class="mb-3">
													<label class="form-label">Email</label> <input type="email"
														class="form-control" placeholder="xxx@abc.com" required>
												</div>
												<div class="mb-3">
													<label class="form-label">姓名</label> <input type="text"
														class="form-control" placeholder="Roger" required>
												</div>
												<div class="mb-3">
													<label class="form-label">手機</label> <input type="tel"
														class="form-control" placeholder="0912345678" required>
												</div>
												<div class="mb-3">
													<label class="form-label">生日</label> <input type="date"
														class="form-control" required>
												</div>
												<div class="mb-3">
													<label class="form-label">密碼</label> <input type="password"
														class="form-control" placeholder="***********" required>
												</div>
												<div class="mb-3">
													<label class="form-label">確認密碼</label> <input
														type="password" class="form-control"
														placeholder="***********" required>
												</div>
												<div class="mb-3">
													<div class="form-check">
														<input type="checkbox" class="form-check-input"
															id="agreeCheck" required> <label
															class="form-check-label" for="agreeCheck">是否為高級權限者</label>
													</div>
												</div>
											</form>
										</div>
										<div class="modal-footer">
											<button type="button" class="btn btn-outline-secondary"
												data-bs-dismiss="modal">取消</button>
											<button type="button" class="btn btn-primary"
												onclick="submitForm()">註冊</button>
										</div>
									</div>
								</div>
							</div>
							<script
								src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
							<script>
								function submitForm() {
									// 這裡添加表單提交邏輯
									alert('註冊成功！');
									var modal = bootstrap.Modal
											.getInstance(document
													.getElementById('addEmployeeModal'));
									modal.hide();
								}
							</script>
</body>
</html>