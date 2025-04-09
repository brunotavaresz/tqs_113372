import api, { createReservation, getReservationByCode, cancelReservation, verifyReservation, checkInReservation } from './api';

export const bookReservation = async (data) => {
  try {
    const response = await createReservation(data);
    return {
      success: true,
      data: response
    };
  } catch (error) {
    console.error('Error booking reservation:', error);
    return {
      success: false,
      error: error.response?.data?.message || 'Failed to book reservation'
    };
  }
};

export const checkReservation = async (code) => {
  try {
    const response = await getReservationByCode(code);
    return {
      success: true,
      data: response
    };
  } catch (error) {
    console.error('Error checking reservation:', error);
    return {
      success: false,
      error: error.response?.data?.message || 'Reservation not found'
    };
  }
};

export const cancelExistingReservation = async (id) => {
  try {
    const response = await cancelReservation(id);
    return {
      success: true,
      data: response
    };
  } catch (error) {
    console.error('Error cancelling reservation:', error);
    return {
      success: false,
      error: error.response?.data?.message || 'Failed to cancel reservation'
    };
  }
};

export const verifyReservationCode = async (code) => {
  try {
    const response = await verifyReservation(code);
    return {
      success: true,
      data: response,
      isValid: response.status === 'ACTIVE'
    };
  } catch (error) {
    console.error('Error verifying reservation:', error);
    return {
      success: false,
      isValid: false,
      error: error.response?.data?.message || 'Invalid reservation code'
    };
  }
};

export const checkInExistingReservation = async (code) => {
  try {
    const response = await checkInReservation(code);
    return {
      success: true,
      data: response
    };
  } catch (error) {
    console.error('Error checking in reservation:', error);
    return {
      success: false,
      error: error.response?.data?.message || 'Failed to check in reservation'
    };
  }
};

export default {
  bookReservation,
  checkReservation,
  cancelExistingReservation,
  verifyReservationCode,
  checkInExistingReservation
};