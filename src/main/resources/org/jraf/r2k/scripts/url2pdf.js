const args = process.argv.slice(2);

const puppeteer = require('puppeteer');

(async () => {
  const browser = await puppeteer.launch();
  const page = await browser.newPage();
  await page.goto(args[0], {
    waitUntil: 'networkidle2',
  });

  // Make all text black, to make it easier to read on Kindle.
  // Also disable ligatures which don't appear correctly on some fonts.
  await page.addStyleTag({ content: '* { color: black!important; font-variant-ligatures: none!important;}' })

  await page.pdf({ path: args[1], format: 'a4', omitBackground: true });

  await browser.close();
})();
