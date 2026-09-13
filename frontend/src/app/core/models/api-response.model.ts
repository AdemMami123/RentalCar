/**
 * Standard API response model
 */
export interface ApiResponse<T> {
  statusCode: number;
  message: string;
  data: T;
  success: boolean;
  timestamp: string;
}
