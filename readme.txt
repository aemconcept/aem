const a = [1, 2, 3, 4, 5];
const b = [2, 4];

const c = a.filter(item => !b.includes(item));

console.log(c); // Output: [1, 3, 5]

=========================================



const emails = [
  "john@example.com",
  "alice@test.com",
  "bob@example.org",
  "carol@sample.com"
];

// Preprocess into [email, domain] pairs
const withDomains = emails.map(email => [email, email.split("@")[1]]);

// Sort by domain
withDomains.sort((a, b) => a[1].localeCompare(b[1]));

// Extract sorted emails
const sortedEmails = withDomains.map(pair => pair[0]);

console.log(sortedEmails);
