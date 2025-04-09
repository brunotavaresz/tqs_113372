/**
 * Formats a date object into a localized string
 * @param {Date} date - The date to format
 * @param {string} format - The format type: 'short', 'medium', 'long', 'weekday'
 * @param {string} locale - The locale to use (defaults to browser locale)
 * @returns {string} Formatted date string
 */
export const formatDate = (date, format = 'medium', locale = navigator.language) => {
    if (!date) return '';
    
    const dateObj = date instanceof Date ? date : new Date(date);
    
    switch (format) {
      case 'short':
        return dateObj.toLocaleDateString(locale, { 
          day: 'numeric', 
          month: 'numeric', 
          year: 'numeric' 
        });
      case 'medium':
        return dateObj.toLocaleDateString(locale, { 
          day: 'numeric', 
          month: 'short', 
          year: 'numeric' 
        });
      case 'long':
        return dateObj.toLocaleDateString(locale, { 
          weekday: 'long', 
          day: 'numeric', 
          month: 'long', 
          year: 'numeric' 
        });
      case 'weekday':
        return dateObj.toLocaleDateString(locale, { weekday: 'long' });
      case 'time':
        return dateObj.toLocaleTimeString(locale, { 
          hour: '2-digit', 
          minute: '2-digit' 
        });
      case 'datetime':
        return `${dateObj.toLocaleDateString(locale, { 
          day: 'numeric', 
          month: 'short' 
        })} ${dateObj.toLocaleTimeString(locale, { 
          hour: '2-digit', 
          minute: '2-digit' 
        })}`;
      default:
        return dateObj.toLocaleDateString(locale);
    }
  };
  
  /**
   * Returns an array of dates for the next n days from a start date
   * @param {Date} startDate - The starting date
   * @param {number} numDays - Number of days to include
   * @returns {Array<Date>} Array of date objects
   */
  export const getNextDays = (startDate = new Date(), numDays = 5) => {
    const dates = [];
    const start = new Date(startDate);
    
    for (let i = 0; i < numDays; i++) {
      const date = new Date(start);
      date.setDate(date.getDate() + i);
      dates.push(date);
    }
    
    return dates;
  };
  
  /**
   * Checks if two dates are the same day
   * @param {Date} date1 - First date to compare
   * @param {Date} date2 - Second date to compare
   * @returns {boolean} True if dates are the same day
   */
  export const isSameDay = (date1, date2) => {
    const d1 = new Date(date1);
    const d2 = new Date(date2);
    
    return (
      d1.getFullYear() === d2.getFullYear() &&
      d1.getMonth() === d2.getMonth() &&
      d1.getDate() === d2.getDate()
    );
  };
  
  export default {
    formatDate,
    getNextDays,
    isSameDay
  };