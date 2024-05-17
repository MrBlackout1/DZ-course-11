import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.ZoneId;

@WebFilter("/time")
public class TimezoneValidateFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }


    @Override
    public void doFilter(javax.servlet.ServletRequest request, javax.servlet.ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String timezone = req.getParameter("timezone");

        if (timezone != null) {
            try {
                ZoneId.of(timezone);
            } catch (Exception e) {
                resp.setContentType("text/html");
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                try (PrintWriter out = resp.getWriter()) {
                    out.println("<html><body>");
                    out.println("<h1>Invalid timezone</h1>");
                    out.println("</body></html>");
                }
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
