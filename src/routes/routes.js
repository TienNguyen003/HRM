/**
 * Routes for Extra Request management (Overtime & Additional Requests)
 */
const router = require('../config/routes');

router.get('/extra_requests', (req, res) => {
  // GET /extra_requests - List all extra requests with filtering
  const params = req.query;
  // Implementation would delegate to ExtraRequestService.search
});

router.post('/extra_requests', (req, res) => {
  // POST /extra_requests - Create new extra request
  // Implementation would delegate to ExtraRequestService.create
});

router.put('/extra_requests/:id', (req, res) => {
  // PUT /extra_requests/:id - Update extra request
  // Implementation would delegate to ExtraRequestService.update
});

router.get('/extra_requests/:id', (req, res) => {
  // GET /extra_requests/:id - Get detail of a specific extra request
  // Implementation would delegate to ExtraRequestService.getById
});

router.put('/extra_requests/:id/status', (req, res) => {
  // PUT /extra_requests/:id/status - Update status of extra request
  // Implementation would delegate to ExtraRequestService.updateStatus
});

router.get('/extra_requests/overtime', (req, res) => {
  // GET /extra_requests/overtime - Get overtime info for a month
  // Implementation would delegate to ExtraRequestService.overtimeInfo
});

router.delete('/extra_requests/:id', (req, res) => {
  // DELETE /extra_requests/:id - Delete extra request
  // Implementation would delegate to ExtraRequestService.delete
});

module.exports = router;
