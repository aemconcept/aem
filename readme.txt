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
