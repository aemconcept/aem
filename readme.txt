const a = [1, 2, 3, 4, 5];
const b = [2, 4];

const c = a.filter(item => !b.includes(item));

console.log(c); // Output: [1, 3, 5]

=========================================



const emails = [
  "jo2hn@google.com", "joh23n@google.com", "jo24hn@google.com", 
  "john@google.com", 
  "alice@test.com",
  "bob@example.org",
  "carol@sample.com"
];

// Preprocess into [email, domain] pairs
const withDomains = emails.map(email => [email, email.split("@")[1]]);

// Sort with priority: google.com domains at top, then alphabetically
withDomains.sort((a, b) => {
  const domainA = a[1];
  const domainB = b[1];

  if (domainA === 'google.com' && domainB !== 'google.com') return -1;
  if (domainB === 'google.com' && domainA !== 'google.com') return 1;
  
  // Normal alphabetical sort
  return domainA.localeCompare(domainB);
});

// Extract sorted emails
const sortedEmails = withDomains.map(pair => pair[0]);

console.log(sortedEmails);


<style>
th {
    background-color: white;
    /* top: calc(100% - 0.5rem); */
    bottom: calc(100% - 0.5rem);
    left: 0;
    max-width: fit-content;
    /* width: 100%; */
    position: absolute;
    z-index: 2;
    padding: 0 0.5rem;
}


const dataList = [
  { id: 1, name: "John" },
  { id: 2, name: "Alice" },
];

fetch("/bin/myservlet", {
  method: "POST",
  headers: {
    "Content-Type": "application/json"
  },
  body: JSON.stringify(dataList)
})
.then(res => res.json())
.then(data => console.log("Success:", data))
.catch(err => console.error("Error:", err));

@SlingServlet(paths = "/bin/myservlet", methods = "POST")
public class MyServlet extends SlingAllMethodsServlet {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {

        // Parse request body to list of MyObject
        List<MyObject> objects = mapper.readValue(
            request.getInputStream(),
            new TypeReference<List<MyObject>>() {}
        );

        // Example: log or process data
        for (MyObject obj : objects) {
            log.info("Received: id={}, name={}", obj.getId(), obj.getName());
        }

        // Send JSON response
        response.setContentType("application/json");
        response.getWriter().write("{\"status\": \"ok\"}");
    }

    // Define your object model
    public static class MyObject {
        private int id;
        private String name;

        // Getters and setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
}

