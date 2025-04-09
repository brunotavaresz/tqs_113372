import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { 
  Box, 
  Typography, 
  Paper, 
  Grid, 
  Button, 
  FormControl, 
  InputLabel, 
  Select, 
  MenuItem, 
  CircularProgress, 
  Alert, 
  Card, 
  CardContent, 
  CardHeader, 
  Divider, 
  LinearProgress,
  IconButton,
  Tooltip
} from '@mui/material';
import { 
  Refresh as RefreshIcon, 
  DeleteSweep as ClearIcon, 
  Cached as CachedIcon,
  Storage as StorageIcon,
  Speed as SpeedIcon
} from '@mui/icons-material';

const CacheMonitor = () => {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [refreshInterval, setRefreshInterval] = useState(30); // seconds
  const [lastRefreshed, setLastRefreshed] = useState(null);
  const [refreshing, setRefreshing] = useState(false);

  // Format number with commas
  const formatNumber = (num) => {
    return num?.toLocaleString() || '0';
  };

  // Format percentage
  const formatPercentage = (value) => {
    return value !== undefined ? `${(value * 100).toFixed(2)}%` : '0%';
  };

  // Format date
  const formatDate = (date) => {
    return date ? new Date(date).toLocaleTimeString() : 'Never';
  };

  // Fetch cache statistics
  const fetchStats = async () => {
    setRefreshing(true);
    try {
      const response = await axios.get('http://localhost:8080/api/weather/cache/stats');
      setStats(response.data);
      setLastRefreshed(new Date());
      setError(null);
    } catch (err) {
      setError('Failed to fetch cache statistics');
      console.error('Error fetching cache statistics:', err);
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  };

  // Clear cache
  const clearCache = async () => {
    try {
      await axios.post('http://localhost:8080/api/weather/cache/clear');
      // Refresh stats after clearing
      fetchStats();
    } catch (err) {
      setError('Failed to clear cache');
      console.error('Error clearing cache:', err);
    }
  };

  // Auto-refresh timer
  useEffect(() => {
    fetchStats(); // Initial fetch
    
    const interval = setInterval(() => {
      fetchStats();
    }, refreshInterval * 1000);
    
    return () => clearInterval(interval); // Cleanup
  }, [refreshInterval]);

  // Handle refresh interval change
  const handleIntervalChange = (e) => {
    const value = e.target.value;
    setRefreshInterval(value);
  };

  // Handle manual refresh
  const handleRefresh = () => {
    fetchStats();
  };

  if (loading && !stats) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '300px' }}>
        <CircularProgress />
        <Typography variant="body1" sx={{ ml: 2 }}>
          Loading cache statistics...
        </Typography>
      </Box>
    );
  }

  return (
    <Box sx={{ maxWidth: 1200, mx: 'auto', p: 3 }}>
      <Paper elevation={3} sx={{ p: 3, mb: 4 }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
          <Typography variant="h4" component="h1" gutterBottom>
            <CachedIcon sx={{ mr: 1, verticalAlign: 'middle' }} />
            Weather Cache Monitor
          </Typography>
          
          <Box sx={{ display: 'flex', alignItems: 'center' }}>
            <Tooltip title="Refresh now">
              <IconButton 
                color="primary" 
                onClick={handleRefresh} 
                disabled={refreshing}
                sx={{ mr: 1 }}
              >
                <RefreshIcon />
              </IconButton>
            </Tooltip>
            
            <FormControl size="small" sx={{ minWidth: 150, mr: 2 }}>
              <InputLabel>Auto-refresh</InputLabel>
              <Select
                value={refreshInterval}
                label="Auto-refresh"
                onChange={handleIntervalChange}
              >
                <MenuItem value={5}>Every 5 seconds</MenuItem>
                <MenuItem value={10}>Every 10 seconds</MenuItem>
                <MenuItem value={30}>Every 30 seconds</MenuItem>
                <MenuItem value={60}>Every minute</MenuItem>
                <MenuItem value={300}>Every 5 minutes</MenuItem>
              </Select>
            </FormControl>
            
            <Tooltip title="Clear cache">
              <Button 
                variant="contained" 
                color="error" 
                startIcon={<ClearIcon />}
                onClick={clearCache}
              >
                Clear Cache
              </Button>
            </Tooltip>
          </Box>
        </Box>
        
        {refreshing && (
          <LinearProgress sx={{ mb: 2 }} />
        )}
        
        <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
          Last refreshed: {formatDate(lastRefreshed)}
        </Typography>
        
        {error && (
          <Alert severity="error" sx={{ mb: 3 }}>{error}</Alert>
        )}
        
        <Grid container spacing={4} alignItems="stretch">
            <Grid item xs={12} md={6}>
                <Card sx={{ height: '100%' }}>
                <CardHeader 
                    title="Cache Performance" 
                    avatar={<SpeedIcon color="primary" />}
                    sx={{ backgroundColor: 'primary.light', color: 'primary.contrastText' }}
                />
                <CardContent>
                    <Box sx={{ mb: 2 }}>
                    <Grid container spacing={2}>
                        <Grid item xs={6}>
                        <Typography variant="body2" color="text.secondary">Total Requests:</Typography>
                        <Typography variant="h6">{formatNumber(stats?.totalRequests)}</Typography>
                        </Grid>
                        <Grid item xs={6}>
                        <Typography variant="body2" color="text.secondary">Hit Rate:</Typography>
                        <Typography variant="h6">{formatPercentage(stats?.hitRate)}</Typography>
                        </Grid>
                    </Grid>
                    </Box>
                    
                    <Divider sx={{ my: 2 }} />
                    
                    <Box sx={{ mb: 2 }}>
                    <Grid container spacing={2}>
                        <Grid item xs={6}>
                        <Typography variant="body2" color="text.secondary">Cache Hits:</Typography>
                        <Typography variant="h6" color="success.main">{formatNumber(stats?.cacheHits)}</Typography>
                        </Grid>
                        <Grid item xs={6}>
                        <Typography variant="body2" color="text.secondary">Cache Misses:</Typography>
                        <Typography variant="h6" color="error.main">{formatNumber(stats?.cacheMisses)}</Typography>
                        </Grid>
                    </Grid>
                    </Box>
                    
                    <Box sx={{ mt: 3 }}>
                    <Typography variant="body2" color="text.secondary" gutterBottom>Hit/Miss Ratio:</Typography>
                    <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                        <Box sx={{ width: '100%', mr: 1 }}>
                        <LinearProgress
                            variant="determinate"
                            value={stats?.hitRate ? stats.hitRate * 100 : 0}
                            color="success"
                            sx={{ 
                            height: 10, 
                            borderRadius: 5,
                            backgroundColor: 'error.light'
                            }}
                        />
                        </Box>
                        <Box sx={{ minWidth: 35 }}>
                        <Typography variant="body2" color="success.main">
                            {formatPercentage(stats?.hitRate)}
                        </Typography>
                        </Box>
                    </Box>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                        <Typography variant="caption" color="success.main">Hits</Typography>
                        <Typography variant="caption" color="error.main">Misses</Typography>
                    </Box>
                    </Box>
                </CardContent>
                </Card>
            </Grid>
            
            <Grid item xs={12} md={6} sx={{ ml: { md: 5 } }}>
                <Card sx={{ height: '100%' }}>
                <CardHeader 
                    title="Cache Details" 
                    avatar={<StorageIcon color="primary" />}
                    sx={{ backgroundColor: 'primary.light', color: 'primary.contrastText' }}
                />
                <CardContent>
                    <Grid container spacing={2}>
                    <Grid item xs={6}>
                        <Typography variant="body2" color="text.secondary">Cache Size:</Typography>
                        <Typography variant="h6">{formatNumber(stats?.cacheSize)} entries</Typography>
                    </Grid>
                    <Grid item xs={6}>
                        <Typography variant="body2" color="text.secondary">TTL:</Typography>
                        <Typography variant="h6">{formatNumber(stats?.cacheTtlSeconds)} seconds</Typography>
                    </Grid>
                    </Grid>
                    
                    <Divider sx={{ my: 3 }} />
                    
                    <Box>
                    <Typography variant="body2" color="text.secondary">Time To Live:</Typography>
                    <Typography variant="body1">
                        {stats?.cacheTtlSeconds && 
                        `${Math.floor(stats.cacheTtlSeconds / 60)} minutes ${stats.cacheTtlSeconds % 60} seconds`}
                    </Typography>
                    </Box>
                    
                    <Box sx={{ mt: 3 }}>
                    <Alert severity="info">
                        <Typography variant="body2">
                        Once cached, weather data will be served from cache for 
                        {stats?.cacheTtlSeconds && ` ${Math.floor(stats.cacheTtlSeconds / 60)} minutes`} 
                        before requesting fresh data from the API.
                        </Typography>
                    </Alert>
                    </Box>
                </CardContent>
                </Card>
            </Grid>
            </Grid>

      </Paper>
    </Box>
  );
};

export default CacheMonitor;