package servlets;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LoginServletTest {
	
	private LoginServlet loginServlet;
	private HttpServletRequest request;
	HttpServletResponse response;
	private HttpSession session;
	
	@Before
	public void setUp(){
		loginServlet = new LoginServlet();
		request = EasyMock.createMock(HttpServletRequest.class);
		response = EasyMock.createMock(HttpServletResponse.class);
		session = EasyMock.createMock(HttpSession.class);
	}
	
	@Test 
	public void testSessionIsNull() {
		EasyMock.expect(request.getSession()).andReturn(null);
		EasyMock.replay(request,session);
		assertEquals(null, request.getSession());
		EasyMock.verify(request,session);
	}
	
//	@Test
//	public void testSessionHasUser()
//	{
//		EasyMock.expect(request.getSession()).andReturn(session);
//    	//session.setAttribute("isAuthenticated", true);
//    	//session.setAttribute("username", request.getParameter("username"));
//		EasyMock.expect(session.getAttribute("isAuthenticated")).andReturn(true);
//		EasyMock.replay(request,session);
//		//assertTrue(loginServlet.isLogin(request));
//		EasyMock.verify(request,session);
//	}
	
	@Test
	public void testIsAuthenticatedAuthenticated() {
		EasyMock.expect(request.getSession(EasyMock.eq(false))).andReturn(session);
		EasyMock.expect(session.getAttribute(EasyMock.eq("isAuthenticated"))).andReturn("true");
		EasyMock.replay(request, session);
		//assertTrue(loginServlet.isAuthenticated(request));
	}

//	@Test
//	public void test() {
//		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
//		System.out.println(request.getParameter("username"));
//		EasyMock.expect(request.getParameter("username")).andReturn("derek").anyTimes();
//		EasyMock.replay(request);
//		EasyMock.verify(request); 
//		
//		System.out.println(request.getParameter("username"));
//	}

//	@Test
//	public void testPost() throws ServletException, IOException {
//		EasyMock.expect(request.getParameter("username")).andReturn("f");
//		EasyMock.expect(request.getSession(false)).andReturn(session);
//		
//		response.setContentType("application/json");
//		response.setStatus(HttpServletResponse.SC_OK);
//		
//		EasyMock.replay(request, response);
//		loginServlet.doPost(request, response);
//		assertEquals("1", request.getParameter("username"), 0);
//		
//		EasyMock.verify(request, response);
//	}
	

	
}
