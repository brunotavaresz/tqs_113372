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
  const [refreshInterval, setRefreshInterval] = useState(30); // segundos
  const [lastRefreshed, setLastRefreshed] = useState(null);
  const [refreshing, setRefreshing] = useState(false);

  // Formatar número com vírgulas
  const formatNumber = (num) => {
    return num?.toLocaleString() || '0';
  };

  // Formatar percentagem
  const formatPercentage = (value) => {
    return value !== undefined ? `${(value * 100).toFixed(2)}%` : '0%';
  };

  // Formatar data
  const formatDate = (date) => {
    return date ? new Date(date).toLocaleTimeString() : 'Nunca';
  };

  // Obter estatísticas do cache
  const fetchStats = async () => {
    setRefreshing(true);
    try {
      const response = await axios.get('http://localhost:8080/api/weather/cache/stats');
      setStats(response.data);
      setLastRefreshed(new Date());
      setError(null);
    } catch (err) {
      setError('Falha ao obter estatísticas do cache');
      console.error('Erro ao obter estatísticas do cache:', err);
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  };

  // Limpar cache
  const clearCache = async () => {
    try {
      await axios.post('http://localhost:8080/api/weather/cache/clear');
      // Atualizar estatísticas após limpar
      fetchStats();
    } catch (err) {
      setError('Falha ao limpar o cache');
      console.error('Erro ao limpar o cache:', err);
    }
  };

  // Temporizador de atualização automática
  useEffect(() => {
    fetchStats(); // Obter inicialmente
    
    const interval = setInterval(() => {
      fetchStats();
    }, refreshInterval * 1000);
    
    return () => clearInterval(interval); // Limpeza
  }, [refreshInterval]);

  // Alterar intervalo de atualização
  const handleIntervalChange = (e) => {
    const value = e.target.value;
    setRefreshInterval(value);
  };

  // Atualização manual
  const handleRefresh = () => {
    fetchStats();
  };

  if (loading && !stats) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '300px' }}>
        <CircularProgress />
        <Typography variant="body1" sx={{ ml: 2 }}>
          A obter estatísticas do cache...
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
            Monitor de Cache de Meteorologia
          </Typography>
          
          <Box sx={{ display: 'flex', alignItems: 'center' }}>
            <Tooltip title="Atualizar agora">
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
              <InputLabel>Atualização automática</InputLabel>
              <Select
                value={refreshInterval}
                label="Atualização automática"
                onChange={handleIntervalChange}
              >
                <MenuItem value={5}>A cada 5 segundos</MenuItem>
                <MenuItem value={10}>A cada 10 segundos</MenuItem>
                <MenuItem value={30}>A cada 30 segundos</MenuItem>
                <MenuItem value={60}>A cada minuto</MenuItem>
                <MenuItem value={300}>A cada 5 minutos</MenuItem>
              </Select>
            </FormControl>
            
            <Tooltip title="Limpar cache">
              <Button 
                variant="contained" 
                color="error" 
                startIcon={<ClearIcon />}
                onClick={clearCache}
              >
                Limpar Cache
              </Button>
            </Tooltip>
          </Box>
        </Box>
        
        {refreshing && (
          <LinearProgress sx={{ mb: 2 }} />
        )}
        
        <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
          Última atualização: {formatDate(lastRefreshed)}
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
                          Os dados meteorológicos serão enviados a partir da cache durante
                          {stats?.cacheTtlSeconds && ` ${Math.floor(stats.cacheTtlSeconds / 60)} min `}
                          para serem obtidos novamente na API.
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
