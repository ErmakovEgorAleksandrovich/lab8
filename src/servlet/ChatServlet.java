package servlet; 
 
import java.util.ArrayList; 
import java.util.HashMap; 
import javax.servlet.ServletException; 
import javax.servlet.http.HttpServlet; 
import entity.ChatMessage; 
import entity.ChatUser; 
 
public class ChatServlet extends HttpServlet { 
   
  private static final long serialVersionUID = 1L; 
   
  //  арта текущих пользователей 
  protected HashMap<String, ChatUser> activeUsers; 
  // —писок сообщений чата 
  protected ArrayList<ChatMessage> messages; 
 
  @SuppressWarnings("unchecked") 
  public void init() throws ServletException { 
    // ¬ызвать унаследованную от HttpServlet версию init() 
    super.init(); 
    // »звлечь из контекста карту пользователей и список сообщений 
    activeUsers = (HashMap<String, ChatUser>) getServletContext().getAttribute("activeUsers"); 
    messages = (ArrayList<ChatMessage>) getServletContext().getAttribute("messages"); 
    // ≈сли карта пользователей не определена ... 

    if (activeUsers==null) { 
      // —оздать новую карту 
      activeUsers = new HashMap<String, ChatUser>(); 
      // ѕоместить ее в контекст сервлета,  
// чтобы другие сервлеты могли до него добратьс€ 
      getServletContext().setAttribute("activeUsers", activeUsers);       
    } 
    // ≈сли список сообщений не определен ... 
    if (messages==null) { 
      // —оздать новый список 
      messages = new ArrayList<ChatMessage>(100); 
      // ѕоместить его в контекст сервлета,  
// чтобы другие сервлеты могли до него добрать 
      getServletContext().setAttribute("messages", messages); 
    } 
  }         
} 
